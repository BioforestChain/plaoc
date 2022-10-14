package org.bfchain.libappmgr.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import org.bfchain.libappmgr.network.SslSettings.getTrustManager
import org.bfchain.libappmgr.network.base.BASE_PORT
import org.bfchain.libappmgr.network.base.BASE_URL
import org.bfchain.libappmgr.utils.AppContextUtil
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object KtorManager {

  val apiService = HttpClient(CIO) {
    defaultRequest { // 给每个HTTP请求加上BaseUrl
      //url(BASE_URL_PATH) { protocol = URLProtocol.HTTPS }
      url {
        protocol = URLProtocol.HTTPS
        host = BASE_URL
      }
    }
    /*engine {
      https {
        trustManager = getTrustManager()
      }
    }*/
    install(ContentNegotiation) { // 引入数据转换插件
      gson()
    }
    install(Logging)
    {
      logger = Logger.SIMPLE // 用于显示本机请求信息
      level = LogLevel.ALL
    }
    install(HttpTimeout) {
      connectTimeoutMillis = 5000
      requestTimeoutMillis = 100000
      //socketTimeoutMillis = 100000
    }
  }
}

/**
 * 用于https请求时的ssl校验：https://ktor.io/docs/client-ssl.html#android
 */
object SslSettings {
  fun getKeyStore(): KeyStore {
    val keyStoreFile = AppContextUtil.sInstance?.assets?.open("keystore.jks")
    // val keyStoreFile = FileInputStream("keystore.jks")
    val keyStorePassword = "foobar".toCharArray()
    val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(keyStoreFile, keyStorePassword)
    return keyStore
  }

  fun getTrustManagerFactory(): TrustManagerFactory? {
    val trustManagerFactory =
      TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(getKeyStore())
    return trustManagerFactory
  }

  fun getSslContext(): SSLContext? {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, getTrustManagerFactory()?.trustManagers, null)
    return sslContext
  }

  fun getTrustManager(): X509TrustManager {
    return getTrustManagerFactory()?.trustManagers?.first { it is X509TrustManager } as X509TrustManager
  }
}
