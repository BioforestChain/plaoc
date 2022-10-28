package org.bfchain.libappmgr.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bfchain.libappmgr.network.ApiService

class MainRepository(private val api: ApiService) {
  suspend fun getAppVersion(path: String) = withContext(Dispatchers.IO) {
    api.getAppVersion()
  }
}
