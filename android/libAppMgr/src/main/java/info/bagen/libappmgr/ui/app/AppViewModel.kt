package info.bagen.libappmgr.ui.app

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.bagen.libappmgr.entity.AppInfo
import info.bagen.libappmgr.entity.AppVersion
import info.bagen.libappmgr.ui.download.DownLoadState
import info.bagen.libappmgr.ui.view.DialogInfo
import info.bagen.libappmgr.ui.view.DialogType
import info.bagen.libappmgr.utils.AppContextUtil
import info.bagen.libappmgr.utils.FilesUtil
import info.bagen.libappmgr.utils.JsonUtil
import kotlinx.coroutines.launch

data class AppViewUIState(
  val useMaskView: Boolean = true, // 表示使用app遮罩的方式下载
  val appViewStateList: ArrayList<AppViewState> = arrayListOf(),
  val appDialogInfo: MutableState<AppDialogInfo> = mutableStateOf(AppDialogInfo()),
  var curAppViewState: AppViewState? = null,
)

data class AppViewState(
  var showBadge: MutableState<Boolean> = mutableStateOf(false),
  var isSystemApp: MutableState<Boolean> = mutableStateOf(true),
  var iconPath: MutableState<String> = mutableStateOf(""),
  var name: MutableState<String> = mutableStateOf(""),
  var versionPath: MutableState<String> = mutableStateOf(""),
  var maskViewState: MutableState<MaskProgressState> = mutableStateOf(MaskProgressState()),
  // var maskDownLoadState: MutableState<DownLoadState> = mutableStateOf(DownLoadState.IDLE),
  var bfsId: String = "",
  var dAppUrl: String = "",
)

data class MaskProgressState(
  var show: Boolean = false,
  var downLoadState: DownLoadState = DownLoadState.IDLE,
  var path: String = ""
)

private fun AppInfo.createAppViewState(
  showBadge: Boolean? = null,
  isSystemApp: Boolean? = null,
  iconPath: String? = null,
  name: String? = null,
  versionPath: String? = null,
  bfsId: String? = null,
  dAppUrl: String = "",
): AppViewState {
  return AppViewState(
    showBadge = showBadge?.let { mutableStateOf(it) } ?: mutableStateOf(false),
    iconPath = iconPath?.let { mutableStateOf(it) } ?: mutableStateOf(this.iconPath),
    name = name?.let { mutableStateOf(it) } ?: mutableStateOf(this.name),
    isSystemApp = isSystemApp?.let { mutableStateOf(it) } ?: mutableStateOf(this.isSystemApp),
    versionPath = versionPath?.let { mutableStateOf(it) } ?: mutableStateOf(this.autoUpdate.url),
    bfsId = bfsId ?: this.bfsAppId,
    dAppUrl = dAppUrl,
  )
}

enum class AppDialogType {
  NewVersion, ReDownLoad, DownLoading, DownLoadCompleted, OpenDApp, Other
}

data class AppDialogInfo(
  val dialogInfo: DialogInfo = DialogInfo(),
  val lastType: AppDialogType = AppDialogType.Other,
  val data: Any? = null
)

sealed class AppViewIntent {
  object LoadAppInfoList : AppViewIntent()
  class LoadAppNewVersion(val appViewState: AppViewState) : AppViewIntent()
  class MaskDownloadCallback(
    val downLoadState: DownLoadState, val dialogInfo: DialogInfo, val appViewState: AppViewState
  ) : AppViewIntent()

  class DialogDownloadCallback(
    val downLoadState: DownLoadState, val dialogInfo: DialogInfo, val appViewState: AppViewState
  ) : AppViewIntent()

  object DialogHide : AppViewIntent()
  object DialogConfirm : AppViewIntent()
}


class AppViewModel(private val repository: AppRepository = AppRepository()) : ViewModel() {
  // val channel = Channel<DownLoadIntent>(2) // 表示最多两个buffer，超过后挂起
  val uiState = mutableStateOf(AppViewUIState())

  /*init { // 这边是初始化的时候执行的内容
    viewModelScope.launch {
      channel.consumeAsFlow().collect { TODO("这功能是持续监听channel信道的内容，有收到就做处理") }
    }
  }*/

  fun handleIntent(action: AppViewIntent) {
    viewModelScope.launch {
      /* channel.send(action) // 进行发送操作，可以根据传参进行发送 */
      when (action) {
        is AppViewIntent.LoadAppInfoList -> loadAppInfoList()
        is AppViewIntent.LoadAppNewVersion -> loadAppNewVersion(action.appViewState)
        is AppViewIntent.DialogHide -> {
          uiState.value.appDialogInfo.value = uiState.value.appDialogInfo.value.copy(
            dialogInfo = DialogInfo(type = DialogType.HIDE)
          )
        }
        is AppViewIntent.MaskDownloadCallback -> maskDownloadCallback(
          action.downLoadState, action.dialogInfo, action.appViewState
        )
        is AppViewIntent.DialogDownloadCallback -> dialogDownloadCallback(
          action.downLoadState, action.dialogInfo, action.appViewState
        )
        is AppViewIntent.DialogConfirm -> dialogConfirm()
      }
    }
  }

  private suspend fun loadAppInfoList() {
    val list = arrayListOf<AppViewState>()
    repository.loadAppInfoList().forEach { appInfo ->
      val appViewState = appInfo.createAppViewState()
      if (appInfo.isSystemApp) {
        appViewState.dAppUrl = repository.loadDAppUrl(appInfo.bfsAppId) // 补充跳转到地址
      }
      list.add(appViewState)
    }
    uiState.value = uiState.value.copy(appViewStateList = list)
  }

  private suspend fun loadAppNewVersion(appViewState: AppViewState) {
    uiState.value.curAppViewState = appViewState
    // 调用下载接口，然后弹出对话框
    val path = FilesUtil.getLastUpdateContent(appViewState.bfsId)
    path?.let {
      JsonUtil.fromJson(AppVersion::class.java, it)?.let { appVersion ->
        dialogShowNewVersion(appVersion)
      }
    }
    repository.loadAppNewVersion(appViewState.versionPath.value).collect { appVersion ->
      dialogShowNewVersion(appVersion)
    }
  }

  private suspend fun dialogShowNewVersion(appVersion: AppVersion?) {
    val url = appVersion?.files?.get(0)?.url ?: ""
    uiState.value.appDialogInfo.value = uiState.value.appDialogInfo.value.copy(
      dialogInfo = DialogInfo(
        type = DialogType.CUSTOM,
        title = "版本更新",
        confirmText = "下载",
        cancelText = "取消",
        text = "版本信息：${appVersion?.version}\n更新内容：${appVersion?.releaseNotes}"
      ), lastType = AppDialogType.NewVersion, data = url
    )
    uiState.value.curAppViewState?.let {
      it.maskViewState.value = it.maskViewState.value.copy(path = url)
    }
  }

  private suspend fun dialogConfirm() {
    when (uiState.value.appDialogInfo.value.lastType) {
      AppDialogType.NewVersion, AppDialogType.ReDownLoad -> {
        if (uiState.value.useMaskView) { // 在app上面显示遮罩
          uiState.value.curAppViewState?.let {
            it.maskViewState.value = it.maskViewState.value.copy(show = true)
          }
          uiState.value.appDialogInfo.value = uiState.value.appDialogInfo.value.copy(
            dialogInfo = DialogInfo(DialogType.HIDE)
          )
        } else { // 打开下载对话框
          uiState.value.appDialogInfo.value = uiState.value.appDialogInfo.value.copy(
            lastType = AppDialogType.DownLoading
          )
        }
      }
      AppDialogType.DownLoadCompleted -> {
        uiState.value.appDialogInfo.value = uiState.value.appDialogInfo.value.copy(
          lastType = AppDialogType.OpenDApp
        )
      }
      AppDialogType.Other -> {}
    }
  }

  private suspend fun dialogDownloadCallback(
    downLoadState: DownLoadState, dialogInfo: DialogInfo, appViewState: AppViewState
  ) {
    val appDialogType: AppDialogType = when (downLoadState) {
      DownLoadState.COMPLETED -> {
        appViewState.showBadge.value = true
        appViewState.isSystemApp.value = true
        appViewState.dAppUrl = repository.loadDAppUrl(appViewState.bfsId) // 跳转需要的地址
        AppDialogType.DownLoadCompleted
      }
      DownLoadState.FAILURE -> AppDialogType.ReDownLoad
      else -> AppDialogType.Other
    }
    uiState.value.appDialogInfo.value = uiState.value.appDialogInfo.value.copy(
      dialogInfo = dialogInfo, lastType = appDialogType
    )
  }

  private suspend fun maskDownloadCallback(
    downLoadState: DownLoadState, dialogInfo: DialogInfo, appViewState: AppViewState
  ) {
    when (downLoadState) {
      DownLoadState.COMPLETED -> {
        appViewState.showBadge.value = true
        appViewState.isSystemApp.value = true
        appViewState.dAppUrl = repository.loadDAppUrl(appViewState.bfsId) // 跳转需要的地址
      }
      DownLoadState.FAILURE -> Toast.makeText(
        AppContextUtil.sInstance!!, dialogInfo.text, Toast.LENGTH_SHORT
      ).show()
      DownLoadState.CLOSE -> appViewState.maskViewState.value =
        appViewState.maskViewState.value.copy(show = false)
    }
    appViewState.maskViewState.value = appViewState.maskViewState.value.copy(
      downLoadState = downLoadState
    )
  }
}
