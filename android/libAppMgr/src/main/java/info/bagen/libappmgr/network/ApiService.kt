package info.bagen.libappmgr.network

import io.ktor.client.statement.*
import info.bagen.libappmgr.entity.AppVersion
import info.bagen.libappmgr.network.base.ApiResultData
import info.bagen.libappmgr.network.base.BaseData
import java.io.File

interface ApiService {

  suspend fun getAppVersion(path: String): ApiResultData<BaseData<AppVersion>>
  suspend fun getAppVersion(): BaseData<AppVersion>

  suspend fun download(path: String, onDownload: (Long, Long) -> Unit): HttpResponse
  suspend fun downloadAndSave(path: String, file: File?, DLProgress: (Long, Long) -> Unit)

  companion object {
    val instance = ApiServiceImpl(KtorManager.apiService)
  }
}
