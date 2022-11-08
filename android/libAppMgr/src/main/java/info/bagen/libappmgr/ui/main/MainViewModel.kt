package info.bagen.libappmgr.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import info.bagen.libappmgr.entity.AppInfo
import info.bagen.libappmgr.entity.AppVersion
import info.bagen.libappmgr.network.ApiService
import info.bagen.libappmgr.network.base.ApiResultData
import info.bagen.libappmgr.network.base.IApiResult
import info.bagen.libappmgr.network.base.fold
import info.bagen.libappmgr.utils.FilesUtil
import info.bagen.libappmgr.utils.JsonUtil

class MainViewModel : ViewModel() {
  fun getAppVersion(appInfo: AppInfo, apiResult: IApiResult<AppVersion>? = null) {
    viewModelScope.launch {
      flow {
        emit(ApiResultData.prepare())
        try {
          Log.d("MainViewModel", "getAppVersion->${appInfo.autoUpdate.url}")
          emit(ApiService.instance.getAppVersion(appInfo.autoUpdate.url))
        } catch (e: Exception) {
          emit(ApiResultData.failure(e))
        }
      }.flowOn(Dispatchers.IO)
        .collect {
          it.fold(
            onSuccess = { baseData ->
              baseData.data?.let {
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
              apiResult?.let {api -> api.onError(-1, "fail", e) }
            },
            onLoading = {}, onPrepare = {}
          )
        }
    }
  }

  fun getAppVersion(path: String, apiResult: IApiResult<AppVersion>? = null) {
    viewModelScope.launch {
      flow {
        emit(ApiResultData.prepare())
        try {
          Log.d("MainViewModel", "getAppVersion(path)->$path")
          emit(ApiService.instance.getAppVersion(path))
        } catch (e: Exception) {
          Log.d("MainViewModel", " 异常 ${e.printStackTrace()}")
          emit(ApiResultData.failure(e))
        }
      }.flowOn(Dispatchers.IO)
        .collect {
          it.fold(
            onSuccess = { baseData ->
              baseData.data?.let {
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
              apiResult?.let {api -> api.onError(-1, "fail", e) }
            },
            onLoading = {}, onPrepare = {}
          )
        }
    }
  }

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
          Log.d("MainViewModel", "getAppVersionAndSave->${appInfo.autoUpdate.url}")
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
                // 将改内容存储到 recommend-app/bfs-id-app/tmp/autoUpdate 中
                FilesUtil.writeFileContent(

                  FilesUtil.getAppVersionSaveFile(appInfo),
                  JsonUtil.toJson(it)
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
              Log.d("MainViewModel", "fail->$e")
              e?.printStackTrace()
              apiResult?.let {api -> api.onError(-1, "fail", e) }
            },
            onLoading = {}, onPrepare = {}
          )
        }
    }
  }

}