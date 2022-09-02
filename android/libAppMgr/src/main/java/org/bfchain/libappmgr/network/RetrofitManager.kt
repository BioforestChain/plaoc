package org.bfchain.libappmgr.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bfchain.libappmgr.network.download.DownloadService
import org.bfchain.libappmgr.utils.jsonPrint
import org.bfchain.libappmgr.utils.kLogger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object RetrofitManager {
    private val logger = kLogger<RetrofitManager>()
    const val BASE_URL = "http://172.30.93.165:8080/"

    /**
     * 所有请求的api都放在这边
     */
    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(ApiCallAdapterFactory())
        .client(genericOkClient())
        .build().create(ApiService::class.java)

    /**
     * 创建下载器
     */
    val downLoadService: DownloadService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .callbackExecutor(Executors.newSingleThreadExecutor()) // 设置线程,如果不设置下载在读取流的时候就会报错
        // .validateEagerly(true) //在开始的时候直接开始检测所有的方法
        .build().create(DownloadService::class.java)

    private fun genericOkClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            logger.jsonPrint(false) { message }
        }

        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(5_000L, TimeUnit.MILLISECONDS)
            .readTimeout(10_000, TimeUnit.MILLISECONDS)
            .writeTimeout(30_000, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

}