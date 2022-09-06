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
import org.bfchain.libappmgr.network.base.*
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.libappmgr.utils.JsonUtil

class MainViewModel : ViewModel() {

    fun getAppVersionAndSave(
        appInfo: AppInfo,
        onSuccess: _SUCCESS<AppVersion>? = null,
        onError: _ERROR? = null
    ) {
        viewModelScope.launch {
            flow {
                try {
                    //emit(ApiService.instance.getAppVersion(appInfo.autoUpdate.url))
                    emit(ApiService.instance.getAppVersion(AppVersionPath))
                } catch (e: Exception) {
                    emit(BaseResultData.failure(e))
                }
            }.flowOn(Dispatchers.IO)
                .collect {
                    it.fold(
                        onSuccess = { appVersion ->
                            Log.d("MainViewModel", "appVersion->$appVersion")
                            appVersion?.let {
                                // 返回成功内容
                                onSuccess?.let { onSuccess(appVersion) }
                                // 将改内容存储到 remember-app/bfs-id-app/tmp/autoUpdate 中
                                FilesUtil.writeFileContent(
                                    FilesUtil.getAppUpdateDirectory(appInfo.bfsAppId),
                                    JsonUtil.toJson(AppVersion::class.java, appVersion)
                                )
                            } ?: onError?.let { onError(NullPointerException()) }
                        },
                        onFailure = { e ->
                            Log.d("MainViewModel", "fail->$e")
                            e?.let { e2 -> onError?.let { onError(e2) } }
                        },
                        onLoading = {}
                    )
                }
        }
    }
}
