package info.bagen.libappmgr.ui.download

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.bagen.libappmgr.network.base.IApiResult
import info.bagen.libappmgr.network.base.fold
import info.bagen.libappmgr.ui.view.DialogInfo
import info.bagen.libappmgr.ui.view.DialogType
import info.bagen.libappmgr.utils.FilesUtil
import info.bagen.libappmgr.utils.ZipUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

data class DownLoadUIState(
  val downLoadState: MutableState<DownLoadState> = mutableStateOf(DownLoadState.IDLE),
  val progress: MutableState<DownLoadProgress> = mutableStateOf(DownLoadProgress()),
  var dialogInfo: DialogInfo = DialogInfo(),
)

enum class DownLoadState { IDLE, LOADING, PAUSE, COMPLETED, FAILURE, INSTALL, CLOSE }

data class DownLoadProgress(
  var current: Long = 0L,
  var total: Long = 0L,
  var progress: Float = 0f,
  var downloadFile: String = "",
  var downloadUrl: String = ""
)

/**
 * MIV中的Intent部分
 */
sealed class DownLoadIntent {
  object DownLoadStop : DownLoadIntent()
  class DownLoadAndSave(val url: String, val saveFile: String) : DownLoadIntent()
}

class DownLoadViewModel(private val repository: DownLoadRepository = DownLoadRepository()) :
  ViewModel() {
  // val channel = Channel<DownLoadIntent>(2) // 表示最多两个buffer，超过后挂起
  val uiState = mutableStateOf(DownLoadUIState())
  var downloadInfo = DownLoadProgress()

  /*init { // 这边是初始化的时候执行的内容
    viewModelScope.launch {
      channel.consumeAsFlow().collect { TODO("这功能是持续监听channel信道的内容，有收到就做处理") }
    }
  }*/

  fun handleIntent(action: DownLoadIntent) {
    viewModelScope.launch {
      /* channel.send(action) // 进行发送操作，可以根据传参进行发送 */
      when (action) {
        is DownLoadIntent.DownLoadAndSave -> {
          // downLoadAndSave(action.url, action.saveFile)
          downloadInfo.downloadFile = action.saveFile
          downloadInfo.downloadUrl = action.url
          downLoadAndSave(action.url, action.saveFile)
        }
        is DownLoadIntent.DownLoadStop -> {
          when (uiState.value.downLoadState.value) {
            DownLoadState.PAUSE -> {
              uiState.value.downLoadState.value = DownLoadState.LOADING
              breakpointDownLoadAndSave(
                downloadInfo.downloadUrl,
                downloadInfo.downloadFile,
                downloadInfo.total
              )
            }
            else -> uiState.value.downLoadState.value = DownLoadState.PAUSE
          }
        }
      }
    }
  }

  private suspend fun downLoadAndSave(url: String, saveFile: String) {
    repository.downLoadAndSave(url, saveFile, isStop = {
      uiState.value.downLoadState.value == DownLoadState.PAUSE // 用来控制是否停止下载
    }, object : IApiResult<Nothing> {
      override fun downloadProgress(current: Long, total: Long, progress: Float) {
        downloadInfo.total = total
        uiState.value.progress.value = uiState.value.progress.value.copy(
          current = current, total = total, progress = progress
        )
      }
    }).flowOn(Dispatchers.IO).collect {
      it.fold(onSuccess = { file ->
        uiState.value.downLoadState.value = DownLoadState.INSTALL
        // 解压文件
        val enableUnzip = ZipUtil.decompress(file.absolutePath, FilesUtil.getAppUnzipPath())
        if (enableUnzip) {
          uiState.value.downLoadState.value = DownLoadState.COMPLETED
          uiState.value.dialogInfo =
            DialogInfo(DialogType.CUSTOM, title = "提示", text = "下载并安装完成", confirmText = "打开")
        } else {
          uiState.value.downLoadState.value = DownLoadState.FAILURE
          uiState.value.dialogInfo = DialogInfo(
            DialogType.CUSTOM,
            title = "异常提示",
            text = "安装失败! 下载的应用无法正常安装，请联系管理员!",
            confirmText = "重新下载"
          )
        }
      }, onFailure = {
        uiState.value.downLoadState.value = DownLoadState.FAILURE
        uiState.value.dialogInfo = DialogInfo(
          DialogType.CUSTOM, title = "异常提示", text = "下载失败! 请联系管理员!", confirmText = "重新下载"
        )
      }, onLoading = {
        uiState.value.downLoadState.value = DownLoadState.LOADING
      }, onPrepare = {
        uiState.value.downLoadState.value = DownLoadState.IDLE
      })
    }
  }

  private suspend fun breakpointDownLoadAndSave(url: String, saveFile: String, total: Long) {
    repository.breakpointDownloadAndSave(url, saveFile, total, isStop = {
      uiState.value.downLoadState.value == DownLoadState.PAUSE // 用来控制是否停止下载
    }, object : IApiResult<Nothing> {
      override fun downloadProgress(current: Long, total: Long, progress: Float) {
        uiState.value.progress.value = uiState.value.progress.value.copy(
          current = current, total = total, progress = progress
        )
      }
    }).flowOn(Dispatchers.IO).collect {
      it.fold(onSuccess = { file ->
        uiState.value.downLoadState.value = DownLoadState.INSTALL
        // 解压文件
        val enableUnzip = ZipUtil.decompress(file.absolutePath, FilesUtil.getAppUnzipPath())
        if (enableUnzip) {
          uiState.value.downLoadState.value = DownLoadState.COMPLETED
          uiState.value.dialogInfo =
            DialogInfo(DialogType.CUSTOM, title = "提示", text = "下载并安装完成", confirmText = "打开")
        } else {
          uiState.value.downLoadState.value = DownLoadState.FAILURE
          uiState.value.dialogInfo = DialogInfo(
            DialogType.CUSTOM,
            title = "异常提示",
            text = "安装失败! 下载的应用无法正常安装，请联系管理员!",
            confirmText = "重新下载"
          )
        }
      }, onFailure = {
        uiState.value.downLoadState.value = DownLoadState.FAILURE
        uiState.value.dialogInfo = DialogInfo(
          DialogType.CUSTOM, title = "异常提示", text = "下载失败! 请联系管理员!", confirmText = "重新下载"
        )
      }, onLoading = {
        uiState.value.downLoadState.value = DownLoadState.LOADING
      }, onPrepare = {
        uiState.value.downLoadState.value = DownLoadState.IDLE
      })
    }
  }
}
