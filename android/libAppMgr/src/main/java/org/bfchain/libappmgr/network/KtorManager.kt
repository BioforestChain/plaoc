package org.bfchain.libappmgr.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import org.bfchain.libappmgr.network.base.BASE_PORT
import org.bfchain.libappmgr.network.base.BASE_URL

object KtorManager {

  val apiService = HttpClient(CIO) {
    defaultRequest {
      host = BASE_URL
      //port = BASE_PORT
      url { protocol = URLProtocol.HTTPS }
    }
    install(ContentNegotiation) { // 引入数据转换插件
      gson()
    }
   /* install(Logging)
    {
      logger = Logger.EMPTY // 用于显示本机请求信息
      level = LogLevel.BODY
    }*/
    install(HttpTimeout) {
      connectTimeoutMillis = 5000
      requestTimeoutMillis = 100000
      //socketTimeoutMillis = 100000
    }
  }
}
