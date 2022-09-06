package org.bfchain.libappmgr.ui.download

import android.util.Log
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
import org.bfchain.libappmgr.network.base.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DownLoadViewModel : ViewModel() {

    fun downloadAndSave(
        path: String, outputFile: String,
        onSuccess: _SUCCESS<File> = {},
        onError: _ERROR = {},
        onProcess: _PROCESS = { _, _, _ -> }
    ) {
        viewModelScope.launch {
            flow {
                try {
                    val httpResponse = ApiService.instance.download(path)
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
                            BaseResultData.progress(
                                currentLength = currentLength,
                                length = length,
                                process = currentLength.toFloat() / length.toFloat()
                            )
                        )
                    }
                    bufferedInputStream.close()
                    outputStream.close()
                    inputStream.close()
                    emit(BaseResultData.success(file))
                } catch (e: Exception) {
                    emit(BaseResultData.failure(e))
                }
            }.flowOn(Dispatchers.IO)
                .collect {
                    it.fold(onFailure = { e ->
                        e?.let { it1 -> onError(it1) }
                    }, onSuccess = { file ->
                        onSuccess(file)
                    }, onLoading = { progress ->
                        onProcess(progress.currentLength, progress.length, progress.process)
                    })
                }
        }
    }
}