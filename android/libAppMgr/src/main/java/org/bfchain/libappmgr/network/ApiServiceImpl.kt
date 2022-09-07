package org.bfchain.libappmgr.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.network.base.ApiResultData
import org.bfchain.libappmgr.network.base.BaseData

class ApiServiceImpl(private val client: HttpClient) : ApiService {
    override suspend fun getAppVersion(path: String): ApiResultData<BaseData<AppVersion>> =
        org.bfchain.libappmgr.network.base.runCatching { client.get(path).body() }

    override suspend fun download(path: String): HttpResponse = client.get(path)
}