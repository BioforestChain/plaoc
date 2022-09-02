package org.bfchain.libappmgr.ui.download

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.bfchain.libappmgr.network.base.download_error
import org.bfchain.libappmgr.network.base.download_process
import org.bfchain.libappmgr.network.base.download_success
import org.bfchain.libappmgr.network.download.DownloadResult
import org.bfchain.libappmgr.network.download.DownloadService
import org.bfchain.libappmgr.network.download.fold
import org.bfchain.libappmgr.utils.FilesUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

class DownloadRepository(private var downloadService: DownloadService) {

    @JvmOverloads
    suspend fun downloadApp(
        url: String, outputFile: String,
        onError: download_error = {},
        onProcess: download_process = { _, _, _ -> },
        onSuccess: download_success = { }
    ) {
        flow {
            try {
                val body = downloadService.downloadApp(url)
                val contentLength = body.contentLength()
                Log.d("DownloadRepository", "totalSize->$contentLength")
                val inputStream = body.byteStream()
                val file = File(outputFile)
                val outputStream = FileOutputStream(file)
                var currentLength = 0
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
                        DownloadResult.progress(
                            currentLength.toLong(),
                            contentLength,
                            currentLength.toFloat() / contentLength.toFloat()
                        )
                    )
                }
                bufferedInputStream.close()
                outputStream.close()
                inputStream.close()
                Log.d("lin.huang", "downloadApp-> emit success")
                emit(DownloadResult.success(file))
                FilesUtil.UnzipFile(outputFile, "/data/user/0/org.bfchain.rust.plaoc/system-app/xxx/sys")
            } catch (e: Exception) {
                Log.d("lin.huang", "downloadApp-> emit exception")
                emit(DownloadResult.failure<File>(e))
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                it.fold(onFailure = { e ->
                    Log.d("lin.huang", "downloadApp.collect-> onFailure")
                    e?.let { it1 -> onError(it1) }
                }, onSuccess = { file ->
                    Log.d("lin.huang", "downloadApp.collect-> onSuccess")
                    onSuccess(file)
                }, onLoading = { progress ->
                    onProcess(progress.currentLength, progress.length, progress.process)
                })
            }
    }
}