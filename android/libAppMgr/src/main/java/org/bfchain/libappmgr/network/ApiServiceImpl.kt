package org.bfchain.libappmgr.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.network.base.ApiResultData
import org.bfchain.libappmgr.network.base.BASE_URL_PATH
import org.bfchain.libappmgr.network.base.BaseData
import java.io.File

class ApiServiceImpl(private val client: HttpClient) : ApiService {
  override suspend fun getAppVersion(path: String): ApiResultData<BaseData<AppVersion>> =
    org.bfchain.libappmgr.network.base.runCatching { client.get(path).body() }

  override suspend fun download(path: String, DLProgress: (Long, Long) -> Unit): HttpResponse =
    client.get(path) {
      onDownload { bytesSentTotal, contentLength ->
        DLProgress(bytesSentTotal, contentLength)
      }
    }

  override suspend fun downloadAndSave(
    path: String,
    file: File?,
    DLProgress: (Long, Long) -> Unit
  ) {
    val builder = HttpRequestBuilder().apply {
      url("$BASE_URL_PATH$path")
    }
    val httpStatement = HttpStatement(builder, client)
    httpStatement.execute { response: HttpResponse ->
      // 下载放在这边实现
      val channel = response.body<ByteReadChannel>()
      val contentLength = response.contentLength() // 文件大小
      requireNotNull(contentLength) { "Header needs to be set by server" }

      var currentLength = 0
      var readBytes: Int
      val bufferSize = 1024 * 8
      var buffer = ByteArray(bufferSize)
      var outputStream = file?.let { it.outputStream() }
      // 读取buffer大小的数据，然后写入文件中，并返回当前进度
      while (channel.readAvailable(buffer, offset = 0, bufferSize).also { readBytes = it } != -1) {
        currentLength += readBytes
        outputStream?.let { it.write(buffer, 0, readBytes) } // 将获取的数据保存到文件
        DLProgress(currentLength.toLong(), contentLength) // 将下载进度回调
      }
      outputStream?.let { it.close() } // 写入完成，关闭*/
    }
  }
}
