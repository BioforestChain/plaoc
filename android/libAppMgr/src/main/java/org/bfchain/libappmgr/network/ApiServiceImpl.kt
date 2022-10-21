package org.bfchain.libappmgr.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import org.bfchain.libappmgr.entity.AppVersion
import org.bfchain.libappmgr.network.base.ApiResultData
import org.bfchain.libappmgr.network.base.BaseData
import org.bfchain.libappmgr.network.base.checkAndBody
import java.io.File

class ApiServiceImpl(private val client: HttpClient) : ApiService {
  override suspend fun getAppVersion(path: String): ApiResultData<BaseData<AppVersion>> =
    org.bfchain.libappmgr.network.base.runCatching {
      client.get(path).checkAndBody()
    }

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
    client.prepareGet(path).execute { httpResponse ->
      val channel: ByteReadChannel = httpResponse.body()
      val contentLength = httpResponse.contentLength() // 文件大小
      var currentLength = 0L
      while (!channel.isClosedForRead) {
        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        while (!packet.isEmpty) {
          val bytes = packet.readBytes()
          currentLength += bytes.size
          file?.appendBytes(bytes)
          DLProgress(currentLength, contentLength!!) // 将下载进度回调
        }
      }
    }

    /*val builder = HttpRequestBuilder().apply {
      url(getUrlPath(path)) {
        protocol = URLProtocol.HTTPS
      }
    }
    val httpStatement = HttpStatement(builder, client)
    httpStatement.execute { response: HttpResponse ->
      if (response.status.value != 200) {
        throw Exception("网络请求异常[$path]->${response.status}")
      }
      // 下载放在这边实现
      val channel = response.content//response.body<ByteReadChannel>()
      val contentLength = response.contentLength() // 文件大小
      requireNotNull(contentLength) { "Header needs to be set by server" }

      var currentLength = 0
      var readBytes: Int
      var buffer = ByteArray(DEFAULT_BUFFER_SIZE)
      var outputStream = file?.let { it.outputStream() }
      // 读取buffer大小的数据，然后写入文件中，并返回当前进度
      while (channel.readAvailable(buffer, offset = 0, DEFAULT_BUFFER_SIZE).also { readBytes = it } != -1) {
        currentLength += readBytes
        outputStream?.let { it.write(buffer, 0, readBytes) } // 将获取的数据保存到文件
        DLProgress(currentLength.toLong(), contentLength) // 将下载进度回调
      }
      outputStream?.let { it.close() } // 写入完成，关闭
    }*/
  }
}
