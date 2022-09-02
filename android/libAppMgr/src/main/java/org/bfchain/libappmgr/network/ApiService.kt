package org.bfchain.libappmgr.network

import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.network.base.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/{app}/appversion.json")
    suspend fun getNewAppVersion(@Path("app") app: String): ApiResult<AppVersion>

}