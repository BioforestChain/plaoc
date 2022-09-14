package org.bfchain.libappmgr.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.model.AppInfo
import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.network.ApiService
import org.bfchain.libappmgr.network.base.ApiResultData
import org.bfchain.libappmgr.network.base.IApiResult
import org.bfchain.libappmgr.network.base.fold
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.libappmgr.utils.JsonUtil

class MainViewModel : ViewModel() {

    fun getAppVersionAndSave(
        appInfo: AppInfo,
        apiResult: IApiResult<AppVersion>? = null
        /*onSuccess: _SUCCESS<AppVersion>? = null,
        onError: _ERROR? = null,
        onPrepare: _ERROR? = null*/
    ) {
        viewModelScope.launch {
            flow {
                emit(ApiResultData.prepare())
                try {
                    Log.d("MainViewModel", "${appInfo.autoUpdate.url}")
                    emit(ApiService.instance.getAppVersion(appInfo.autoUpdate.url))
                    // emit(ApiService.instance.getAppVersion(AppVersionPath))
                } catch (e: Exception) {
                    emit(ApiResultData.failure(e))
                }
            }.flowOn(Dispatchers.IO)
                .collect {
                    it.fold(
                        onSuccess = { baseData ->
                            Log.d("MainViewModel", "baseData->${baseData}")
                            Log.d("MainViewModel", "appVersion->${baseData.data}")
                            baseData.data?.let {
                                // 将改内容存储到 remember-app/bfs-id-app/tmp/autoUpdate 中
                                FilesUtil.writeFileContent(
                                    FilesUtil.getAppVersionSaveFile(appInfo),
                                    JsonUtil.toJson(AppVersion::class.java, baseData.data)
                                )
                                apiResult?.let {
                                    apiResult.onSuccess(
                                        baseData.errorCode,
                                        baseData.errorMsg,
                                        baseData.data
                                    )
                                }
                            }
                        },
                        onFailure = { e ->
                            //Log.d("MainViewModel", "fail->$e")
                            e?.let { e1 -> apiResult?.let { apiResult.onError(-1, "fail", e1) } }
                        },
                        onLoading = {}, onPrepare = {}
                    )
                }
        }
    }
}
