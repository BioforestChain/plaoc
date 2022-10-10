package org.bfchain.libappmgr.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.network.ApiService
import org.bfchain.libappmgr.network.base.ApiResultData
import org.bfchain.libappmgr.network.base.IApiResult
import org.bfchain.libappmgr.network.base.fold
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DownLoadViewModel : ViewModel() {

  fun downloadAndSave(url: String, outputFile: String, apiResult: IApiResult<Nothing>) {
    viewModelScope.launch {
      flow {
        emit(ApiResultData.prepare())
        try {
          var file = File(outputFile)
          ApiService.instance.downloadAndSave(url, file) { current, total ->
            var progress = current.toFloat() / (total)
            apiResult.downloadProgress(current, total, progress)
          }
          delay(100)

          emit(ApiResultData.success(file))
        } catch (e: Exception) {
          emit(ApiResultData.failure(e))
        }
      }.flowOn(Dispatchers.IO)
        .collect() {
          it.fold(onFailure = { e ->
            e?.let { it1 -> apiResult.onError(-1, it1.message!!, it1) }
          }, onSuccess = { file ->
            apiResult.downloadSuccess(file)
          }, onLoading = { progress ->
            apiResult.downloadProgress(
              progress.currentLength,
              progress.length,
              progress.process
            )
          }, onPrepare = {
            apiResult.onPrepare()
          })
        }
    }
  }

  /*fun downloadAndSave(url: String, outputFile: String, apiResult: IApiResult<Nothing>) {
    viewModelScope.launch {
      flow {
        emit(ApiResultData.prepare())
        try {

          val httpResponse = ApiService.instance.download(url, DLProgress = { current, total ->
            var progress = current.toFloat() / (total * 2) // 下载占用一半
            apiResult.downloadProgress(current, total, progress)
          })

          val file = File(outputFile)
          val length = httpResponse.contentLength() ?: 0
          val outputStream = FileOutputStream(file)
          var currentLength: Long = 0
          val inputStream: InputStream = httpResponse.bodyAsChannel().toInputStream()
          val bufferSize = 1024 * 8
          val buffer = ByteArray(bufferSize)
          val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
          var readLength: Int
          while (bufferedInputStream.read(buffer, 0, bufferSize)
              .also { readLength = it } != -1
          ) {
            outputStream.write(buffer, 0, readLength)
            currentLength += readLength
            var progress = currentLength.toFloat() / (length * 2) + 0.5f // 存储占用一半
            emit(ApiResultData.progress(currentLength, length, progress))
          }
          emit(ApiResultData.progress(length, length, 1.0f)) // 下载和存储完成后显示进度100%
          delay(100)
          bufferedInputStream.close()
          outputStream.close()
          inputStream.close()
          emit(ApiResultData.success(file))
        } catch (e: Exception) {
          emit(ApiResultData.failure(e))
        }
      }.flowOn(Dispatchers.IO)
        .collect {
          it.fold(onFailure = { e ->
            e?.let { it1 -> apiResult.onError(-1, it1.message!!, it1) }
          }, onSuccess = { file ->
            apiResult.downloadSuccess(file)
          }, onLoading = { progress ->
            apiResult.downloadProgress(
              progress.currentLength,
              progress.length,
              progress.process
            )
          }, onPrepare = {
            apiResult.onPrepare()
          })
        }
    }
  }*/
}
