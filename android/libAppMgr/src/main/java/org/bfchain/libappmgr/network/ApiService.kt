package org.bfchain.libappmgr.network

import io.ktor.client.statement.*
import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.network.base.BaseResultData

interface ApiService {

    suspend fun getAppVersion(path: String): BaseResultData<AppVersion>

    suspend fun download(path: String): HttpResponse

    companion object {
        val instance = ApiServiceImpl(KtorManager.apiService)
    }
}