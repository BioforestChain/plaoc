package org.bfchain.libappmgr.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
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

    fun downloadAndSave(
        url: String, outputFile: String, apiResult: IApiResult<Nothing>
        /*onSuccess: _SUCCESS<File> = {},
        onError: _ERROR = {},
        onProcess: _PROCESS = { _, _, _ -> }*/
    ) {
        viewModelScope.launch {
            flow {
                emit(ApiResultData.prepare())
                try {
                    val httpResponse = ApiService.instance.download(url)
                    val length: Long = httpResponse.contentLength() ?: 0
                    val file = File(outputFile)
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
                        emit(
                            ApiResultData.progress(
                                currentLength = currentLength,
                                length = length,
                                process = currentLength.toFloat() / length.toFloat()
                            )
                        )
                    }
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
                        e?.let { it1 -> apiResult.onError(-1, "请求失败", it1) }
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
}