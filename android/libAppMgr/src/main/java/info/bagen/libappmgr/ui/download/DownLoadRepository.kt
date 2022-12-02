package info.bagen.libappmgr.ui.download

import info.bagen.libappmgr.network.ApiService
import info.bagen.libappmgr.network.base.ApiResultData
import info.bagen.libappmgr.network.base.IApiResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class DownLoadRepository(private val api: ApiService = ApiService.instance) {

  suspend fun downLoadAndSave(
    url: String, saveFile: String, isStop: () -> Boolean, apiResult: IApiResult<Nothing>
  ): Flow<ApiResultData<File>> = flow {
    // emit(ApiResultData.prepare()) // 通知修改状态为准备
    try {
      emit(ApiResultData.progress()) // 通知修改状态为加载
      var file = File(saveFile)
      var contentLength = 0L
      api.downloadAndSave(url, file, isStop) { current, total ->
        contentLength = total
        val progress = current.toFloat() / (total)
        apiResult.downloadProgress(current, total, progress) // 进度需要单独处理，因为emit必须在协程中调用
      }
      if (file.length() == contentLength) {
        delay(1000)
        emit(ApiResultData.success(file))
      }
    } catch (e: Exception) {
      emit(ApiResultData.failure(e))
    }
  }

  /**
   * 实现断点下载功能
   */
  suspend fun breakpointDownloadAndSave(
    url: String,
    saveFile: String,
    total: Long,
    isStop: () -> Boolean,
    apiResult: IApiResult<Nothing>
  ): Flow<ApiResultData<File>> = flow {
    try {
      val file = File(saveFile)
      var contentLength = 0L
      api.breakpointDownloadAndSave(url, file, total, isStop = isStop) { current, total ->
        contentLength = total
        val progress = current.toFloat() / (total)
        apiResult.downloadProgress(current, total, progress) // 进度需要单独处理，因为emit必须在协程中调用
      }
      if (file.length() == contentLength) {
        delay(1000)
        emit(ApiResultData.success(file))
      }
    } catch (e: Exception) {
      emit(ApiResultData.failure(e))
    }
  }
}
