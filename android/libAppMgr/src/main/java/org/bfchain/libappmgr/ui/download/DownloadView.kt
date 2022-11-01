package org.bfchain.libappmgr.ui.download

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.data.*
import org.bfchain.libappmgr.entity.AppInfo
import org.bfchain.libappmgr.entity.AppVersion
import org.bfchain.libappmgr.entity.DAppInfo
import org.bfchain.libappmgr.entity.DownLoadState
import org.bfchain.libappmgr.network.base.IApiResult
import org.bfchain.libappmgr.ui.main.MainViewModel
import org.bfchain.libappmgr.ui.main.MainViewModel2
import org.bfchain.libappmgr.ui.view.*
import org.bfchain.libappmgr.utils.AppContextUtil
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.libappmgr.utils.JsonUtil
import org.bfchain.libappmgr.utils.ZipUtil
import java.io.File

/**
 * 下面界面显示在图标上
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun DownloadAppInfoView(
  appInfo: AppInfo,
  onOpenApp: ((appId: String, url: String) -> Unit)? = null
) {
  var coroutineScope = rememberCoroutineScope()
  var vmMain: MainViewModel = viewModel()
  var vmDown: DownLoadViewModel = viewModel()

  var customDialogData = CustomDialogData(confirmText = mutableStateOf("下载"))
  var dialogState = rememberDialogAllState(customDialogData)
  var maskProgressMode = rememberMaskProgressMode()
  var appInfoMode = createAppInfoMode(appInfo.iconPath, appInfo.name)
  var appVersion: AppVersion? = null
  var dAppInfo: DAppInfo? = null

  AppInfoView(
    appInfoMode = appInfoMode,
    maskProgressMode = maskProgressMode,
    onMaskProgressClick = {
      /*when (appInfoMode.downLoadState.value) {
        DownLoadState.LOADING -> {
          appInfoMode.updateDLState(DownLoadState.PAUSE).updateName("暂停下载")
        }
        DownLoadState.PAUSE -> {
          appInfoMode.updateDLState(DownLoadState.LOADING).updateName("正在下载")
        }
      }*/
    },
    onClick = {
      if (!appInfo.isSystemApp) {
        dialogState.updateState(customShow = true)
        var requestFinish = false
        coroutineScope.launch {
          var version = FilesUtil.getLastUpdateContent(appInfo)?.let {
            JsonUtil.fromJson(AppVersion::class.java, it)
          }
          if (!requestFinish) {
            appVersion = version
            customDialogData.updateNewVersion(appVersion = appVersion)
          }
        }
        vmMain.getAppVersionAndSave(appInfo, object : IApiResult<AppVersion> {
          override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
            requestFinish = true
            appVersion = data
            customDialogData.updateNewVersion(appVersion = appVersion)
          }
        })
      } else {
        // 1.打开应用
        dAppInfo = FilesUtil.getDAppInfo(appInfo.bfsAppId)
        dAppInfo?.let { dApp ->
          onOpenApp?.let {
            onOpenApp(
              appInfo.bfsAppId,
              getDAppInfoUrl(appInfo, dApp)
            )
          } // 回调dweb请求的地址
          Toast.makeText(
            AppContextUtil.sInstance, "直接打开应用-->${dApp.manifest.bfsaEntry}", Toast.LENGTH_SHORT
          ).show()
          appInfoMode.showBadge.value = false // 打开后，隐藏小红点
        }
        vmMain.getAppVersion(appInfo, object : IApiResult<AppVersion> {
          override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
            // 请求成功后，比对获取的版本号和当前版本号
            if (dAppInfo == null || hasNewVersion(dAppInfo!!.manifest.version, data.version)) {
              appVersion = data
              dialogState.updateState(customShow = true).customDialog.updateNewVersion(appVersion)
            }
          }
        })
      }
    })

  CustomAlertDialog(state = dialogState, onConfirm = {
    if (appVersion != null && appVersion!!.files != null && appVersion!!.files!!.isNotEmpty()) {
      dialogState.updateState(customShow = false)
      maskProgressMode.updateState(DownLoadState.LOADING)
      vmDown.downloadAndSave(
        appVersion!!.files!![0].url,
        FilesUtil.getAppDownloadPath(),
        object : IApiResult<Nothing> {
          override fun downloadProgress(current: Long, total: Long, progress: Float) {
            appInfoMode.updateDLState(DownLoadState.LOADING).updateName("正在下载")
            maskProgressMode.progress(progress)
          }

          override fun downloadSuccess(file: File) {
            GlobalScope.launch {
              // 显示当前状态
              appInfoMode.updateDLState(DownLoadState.LOADING).updateName("正在解压")
              // 解压耗时
              ZipUtil.decompress(file.absolutePath, FilesUtil.getAppUnzipPath(appInfo))
              appInfo.isSystemApp = true
              maskProgressMode.updateState(DownLoadState.IDLE)
              appInfoMode.updateShowBadge(true).updateDLState(DownLoadState.COMPLETED)
                .updateName(appInfo.name)
            }
          }

          override fun onError(errorCode: Int, errorMsg: String, exception: Throwable?) {
            // 隐藏下载进度界面，显示对话框界面
            dialogState.updateState(customShow = true).customDialog.update(
              title = "异常提醒", content = errorMsg, confirmText = "重新下载"
            )
            maskProgressMode.updateState(DownLoadState.IDLE)
          }
        })
    } else {
      maskProgressMode.updateState(DownLoadState.IDLE)
      dialogState.updateState(customShow = true).customDialog.update(
        title = "异常提醒", content = "下载失败，找不到版本信息", confirmText = "重新下载"
      )
    }
  })
}

/**
 * 基于Android类型的下载界面
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun DownloadDialogView(
  appInfo: AppInfo,
  onOpenApp: ((appId: String, url: String) -> Unit)? = null
) {
  var coroutineScope = rememberCoroutineScope()
  var vmMain: MainViewModel = viewModel()
  var vmDown: DownLoadViewModel = viewModel()

  var customDialogData = CustomDialogData(confirmText = mutableStateOf("下载"))
  var dialogState = rememberDialogAllState(customDialogData)
  var appInfoMode = createAppInfoMode(appInfo.iconPath, appInfo.name)
  var appVersion: AppVersion? = null
  var dAppInfo: DAppInfo? = null

  // 显示界面图标
  AppInfoView(
    appInfoMode = appInfoMode,
    onClick = {
      if (!appInfo.isSystemApp) {
        dialogState.updateState(customShow = true)
        var requestFinish = false
        coroutineScope.launch {
          var version = FilesUtil.getLastUpdateContent(appInfo)?.let {
            JsonUtil.fromJson(AppVersion::class.java, it)
          }
          if (!requestFinish) {
            appVersion = version
            customDialogData.updateNewVersion(appVersion = appVersion)
          }
        }
        vmMain.getAppVersionAndSave(appInfo, object : IApiResult<AppVersion> {
          override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
            requestFinish = true
            appVersion = data
            customDialogData.updateNewVersion(appVersion = appVersion)
          }
        })
      } else {
        // 1.打开应用
        dAppInfo = FilesUtil.getDAppInfo(appInfo.bfsAppId)
        dAppInfo?.let { dApp ->
          onOpenApp?.let {
            onOpenApp(
              appInfo.bfsAppId,
              getDAppInfoUrl(appInfo, dApp)
            )
          } // 回调dweb请求的地址
          Toast.makeText(
            AppContextUtil.sInstance, "直接打开应用-->${dApp.manifest.bfsaEntry}", Toast.LENGTH_SHORT
          ).show()
          appInfoMode.showBadge.value = false // 打开后，隐藏小红点
        }
        vmMain.getAppVersion(appInfo, object : IApiResult<AppVersion> {
          override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
            // 请求成功后，比对获取的版本号和当前版本号
            if (dAppInfo == null || hasNewVersion(dAppInfo!!.manifest.version, data.version)) {
              appVersion = data
              dialogState.updateState(customShow = true).customDialog.updateNewVersion(appVersion)
            }
          }
        })
      }
    })
  // 显示提示对话框
  CustomAlertDialog(dialogState, onConfirm = {
    if (appInfoMode.downLoadState.value == DownLoadState.COMPLETED) {
      dialogState.updateState(customShow = false)
      appInfoMode.downLoadState.value = DownLoadState.IDLE
      if (dAppInfo != null) {
        var url = getDAppInfoUrl(appInfo, dAppInfo!!)
        Toast.makeText(
          AppContextUtil.sInstance, "直接打开应用-->${appInfo.name} url->$url", Toast.LENGTH_SHORT
        ).show()
        onOpenApp?.let { onOpenApp(appInfo.bfsAppId, url) }
      }
      appInfoMode.showBadge.value = false // 打开后，隐藏小红点
    } else if (appVersion != null && appVersion!!.files != null && appVersion!!.files!!.isNotEmpty()) {
      // 调用下载操作
      dialogState.updateState(progressShow = true)
      appInfoMode.downLoadState.value = DownLoadState.LOADING
      vmDown.downloadAndSave(
        appVersion!!.files!![0].url,
        FilesUtil.getAppDownloadPath(),
        object : IApiResult<Nothing> {
          override fun downloadProgress(current: Long, total: Long, progress: Float) {
            dialogState.progressDialog.progress.value = progress
          }

          override fun downloadSuccess(file: File) {
            GlobalScope.launch {
              ZipUtil.decompress(file.absolutePath, FilesUtil.getAppUnzipPath(appInfo))
              // 隐藏下载进度界面，显示对话框界面
              dialogState.updateState(customShow = true).customDialog.update(
                content = "下载完成", confirmText = "打开"
              )
              appInfo.isSystemApp = true
              appInfoMode.downLoadState.value = DownLoadState.COMPLETED
              appInfoMode.showBadge.value = true
            }
          }

          override fun onError(errorCode: Int, errorMsg: String, exception: Throwable?) {
            // 隐藏下载进度界面，显示对话框界面
            dialogState.updateState(customShow = true).customDialog.update(
              title = "异常提醒", content = errorMsg, confirmText = "重新下载"
            )
            appInfoMode.downLoadState.value = DownLoadState.IDLE
          }
        })
    } else {
      appInfoMode.downLoadState.value = DownLoadState.IDLE
      dialogState.updateState(customShow = true).customDialog.update(
        title = "异常提醒", content = "下载失败，找不到版本信息", confirmText = "重新下载"
      )
    }
  }, onCancel = { appInfoMode.downLoadState.value = DownLoadState.IDLE })
  // 显示正在请求对话框
  // LoadingDialog(dialogState)
  // 显示下载进度对话框
  ProgressDialog(dialogState, onCancel = { appInfoMode.downLoadState.value = DownLoadState.IDLE })
}

private fun getDAppInfoUrl(appInfo: AppInfo, dAppInfo: DAppInfo): String {
  // return FilesUtil.getDAppInfo(appInfo)?.let { it.enter.main } ?: ""
  return FilesUtil.getAppDenoUrl(appInfo.bfsAppId, dAppInfo.manifest.bfsaEntry)
}

private fun hasNewVersion(cur: String, net: String): Boolean {
  var curArray = cur.split(".")
  var netArray = net.split(".")
  var minSize = when (curArray.size > netArray.size) {
    true -> netArray.size
    false -> curArray.size
  }
  for (i in (0 until minSize)) {
    if (curArray[i].toInt() < netArray[i].toInt()) {
      return true
    }
  }
  if (curArray.size < netArray.size) return true
  return false
}
