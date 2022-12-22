package info.bagen.libappmgr.ui.app

import info.bagen.libappmgr.entity.DAppInfoUI
import info.bagen.libappmgr.network.ApiService
import info.bagen.libappmgr.network.base.fold
import info.bagen.libappmgr.utils.APP_DIR_TYPE
import info.bagen.libappmgr.utils.FilesUtil
import info.bagen.libappmgr.utils.parseFilePath
import kotlinx.coroutines.flow.flow

class AppRepository(private val api: ApiService = ApiService.instance) {

  suspend fun loadAppInfoList() = FilesUtil.getAppInfoList()

  suspend fun loadDAppUrl(bfsId: String): DAppInfoUI? {
    val dAppInfo = FilesUtil.getDAppInfo(bfsId)
    dAppInfo?.let { info ->
      val dAppUrl = FilesUtil.getAppDenoUrl(bfsId, info.manifest.bfsaEntry)
      val url = info.manifest.url ?: ""
      val isDWeb = info.manifest.appType != "web"
      val name = dAppInfo.manifest.name
      val icon = FilesUtil.getAppIconPathName(
        bfsId, dAppInfo.manifest.icon.parseFilePath(), APP_DIR_TYPE.SystemApp
      )
      val version = dAppInfo.manifest.version
      return DAppInfoUI(dAppUrl, name, icon, url, version, isDWeb)
    }
    return null
  }

  suspend fun loadAppNewVersion(path: String) = flow {
    val a = api.getAppVersion(path)
    a.fold(
      onSuccess = { emit(it.data) },
      onFailure = {},
      onLoading = {},
      onPrepare = {}
    )
  }
}
