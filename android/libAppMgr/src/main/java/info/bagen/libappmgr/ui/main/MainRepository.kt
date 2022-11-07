package info.bagen.libappmgr.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import info.bagen.libappmgr.network.ApiService

class MainRepository(private val api: ApiService) {
  suspend fun getAppVersion(path: String) = withContext(Dispatchers.IO) {
    api.getAppVersion()
  }
}
