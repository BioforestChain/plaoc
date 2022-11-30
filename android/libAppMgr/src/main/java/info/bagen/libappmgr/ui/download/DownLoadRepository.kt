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
    url: String, saveFile: String, apiResult: IApiResult<Nothing>
  ): Flow<ApiResultData<File>> = flow {
    // emit(ApiResultData.prepare()) // 通知修改状态为准备
    try {
      emit(ApiResultData.progress()) // 通知修改状态为加载
      var file = File(saveFile)
      api.downloadAndSave(url, file) { current, total ->
        var progress = current.toFloat() / (total)
        apiResult.downloadProgress(current, total, progress) // 进度需要单独处理，因为emit必须在协程中调用
      }
      delay(1000)

      emit(ApiResultData.success(file))
    } catch (e: Exception) {
      emit(ApiResultData.failure(e))
    }
  }
}
