package info.bagen.libappmgr.network

import info.bagen.libappmgr.entity.AppVersion
import info.bagen.libappmgr.network.base.ApiResultData
import info.bagen.libappmgr.network.base.BaseData
import info.bagen.libappmgr.network.base.bodyData
import info.bagen.libappmgr.network.base.checkAndBody
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import java.io.File

class ApiServiceImpl(private val client: HttpClient) : ApiService {
  override suspend fun getAppVersion(path: String): ApiResultData<BaseData<AppVersion>> =
    info.bagen.libappmgr.network.base.runCatching {
      client.get(path).checkAndBody()
    }

  override suspend fun getAppVersion(): BaseData<AppVersion> =
    client.get("KEJPMHLA/appversion.json").bodyData()

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
      if (!httpResponse.status.isSuccess()) { // 如果网络请求失败，直接抛异常
        throw(java.lang.Exception(httpResponse.status.toString()))
      }
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
  }
}
