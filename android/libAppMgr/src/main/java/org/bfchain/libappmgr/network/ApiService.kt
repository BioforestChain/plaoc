package org.bfchain.libappmgr.network

import io.ktor.client.statement.*
import org.bfchain.libappmgr.entity.AppVersion
import org.bfchain.libappmgr.network.base.ApiResultData
import org.bfchain.libappmgr.network.base.BaseData
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
