package org.bfchain.libappmgr.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import org.bfchain.libappmgr.network.base.BASE_URL

object KtorManager {

  val apiService = HttpClient(CIO) {
    defaultRequest { // 给每个HTTP请求加上BaseUrl
      host = BASE_URL
      // port = BASE_PORT
      url { protocol = URLProtocol.HTTPS }
    }
    install(ContentNegotiation) { // 引入数据转换插件
      gson()
    }
    install(Logging)
    {
      level = LogLevel.ALL
    }
    install(HttpTimeout) {
      connectTimeoutMillis = 5000
      requestTimeoutMillis = 100000
      //socketTimeoutMillis = 100000
    }
  }
}
