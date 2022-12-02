package info.bagen.libappmgr.ui.app

import info.bagen.libappmgr.network.ApiService
import info.bagen.libappmgr.network.base.fold
import info.bagen.libappmgr.utils.FilesUtil
import kotlinx.coroutines.flow.flow

class AppRepository(private val api: ApiService = ApiService.instance) {

  suspend fun loadAppInfoList() = FilesUtil.getAppInfoList()

  suspend fun loadDAppUrl(bfsId: String): String {
    return FilesUtil.getDAppInfo(bfsId)?.let {
      FilesUtil.getAppDenoUrl(bfsId, it.manifest.bfsaEntry)
    } ?: ""
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
