package org.bfchain.libappmgr.ui.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.R
import org.bfchain.libappmgr.data.*
import org.bfchain.libappmgr.entity.AppInfo
import org.bfchain.libappmgr.entity.AppVersion
import org.bfchain.libappmgr.entity.AutoUpdateInfo
import org.bfchain.libappmgr.network.base.IApiResult
import org.bfchain.libappmgr.ui.download.DownLoadViewModel
import org.bfchain.libappmgr.ui.main.MainViewModel
import org.bfchain.libappmgr.utils.AppContextUtil
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.libappmgr.utils.JsonUtil
import java.io.File

enum class DownloadType { Init, Process, Complete, Fail }

@Composable
fun AppInfoView(
  showBadge: MutableState<Boolean>,
  appInfo: AppInfo,
  onClick: ((appInfo: AppInfo) -> Unit)? = null
) {
  Box(
    modifier = Modifier
      .width(60.dp)
      .height(120.dp)
      .padding(6.dp)
  ) {
    Column(modifier = Modifier.align(Alignment.Center)) {
      Box(
        modifier = Modifier
          .width(60.dp)
          .height(60.dp)
          .clickable { onClick?.let { onClick(appInfo) } }
      ) {
        Log.d("AppView", "iconPath->${appInfo.iconPath}")
        Image(
          bitmap = BitmapFactory.decodeFile(appInfo.iconPath)?.asImageBitmap()
            ?: ImageBitmap.imageResource(id = R.drawable.ic_launcher),
          contentDescription = "icon",
          contentScale = ContentScale.FillWidth,
          modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(12.dp))
        )

        if (showBadge.value) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .size(12.dp)
              .background(Color.Red)
              .align(Alignment.TopEnd)
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .width(60.dp)
          .height(40.dp)
          .align(Alignment.CenterHorizontally)
      ) {
        Text(
          text = appInfo.name,
          maxLines = 2,
          color = Color.White,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun AppInfoView(appInfo: AppInfo, onOpenApp: ((url: String) -> Unit)? = null) {
  var coroutineScope = rememberCoroutineScope()
  var vmMain: MainViewModel = viewModel()
  var vmDown: DownLoadViewModel = viewModel()

  var customDialogData = CustomDialogData(confirmText = mutableStateOf("下载"))
  var dialogState = rememberDialogAllState(customDialogData)
  var showBadge = remember { mutableStateOf(appInfo.isShowBadge) }
  var appVersion: AppVersion? = null
  var downloadType = DownloadType.Init

  // 显示界面图标
  AppInfoView(showBadge = showBadge, appInfo = appInfo, onClick = {
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
      var dappInfo = FilesUtil.getDAppInfo(appInfo)
      dappInfo?.let { dApp ->
        onOpenApp?.let { onOpenApp(dApp.enter.main) } // 回调dweb请求的地址
        Toast.makeText(
          AppContextUtil.sInstance, "直接打开应用-->${dApp.enter.main}", Toast.LENGTH_SHORT
        ).show()
        appInfo.isShowBadge = false // 打开后，隐藏小红点
        showBadge.value = false
      }
      vmMain.getAppVersion(appInfo, object : IApiResult<AppVersion> {
        override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
          // 请求成功后，比对获取的版本号和当前版本号
          if (dappInfo == null || hasNewVersion(dappInfo.version, data.version)) {
            appVersion = data
            dialogState.updateState(customShow = true).customDialog.updateNewVersion(appVersion)
          }
        }
      })
    }
  })
  // 显示提示对话框
  CustomAlertDialog(dialogState, onConfirm = {
    if (downloadType == DownloadType.Complete) {
      dialogState.updateState(customShow = false)
      downloadType = DownloadType.Init
      var url = getDAppInfoUrl(appInfo)
      Toast.makeText(
        AppContextUtil.sInstance, "直接打开应用-->${appInfo.name} url->$url", Toast.LENGTH_SHORT
      ).show()
      onOpenApp?.let { onOpenApp(url) }
      appInfo.isShowBadge = false // 打开后，隐藏小红点
      showBadge.value = false
    } else if (appVersion != null && appVersion!!.files.isNotEmpty()) {
      // 调用下载操作
      dialogState.updateState(progressShow = true)
      downloadType = DownloadType.Process
      vmDown.downloadAndSave(
        appVersion!!.files[0].url,
        FilesUtil.getAppDownloadPath(),
        object : IApiResult<Nothing> {
          override fun downloadProgress(current: Long, total: Long, progress: Float) {
            dialogState.progressDialog.progress.value = progress
            downloadType = DownloadType.Process
          }

          override fun downloadSuccess(file: File) {
            FilesUtil.UnzipFile(
              file.absolutePath,
              desDirectory = FilesUtil.getAppUnzipPath(appInfo)
            )
            file.delete() // 解压后，删除压缩包
            // 隐藏下载进度界面，显示对话框界面
            dialogState.updateState(customShow = true).customDialog.update(
              content = "下载完成", confirmText = "打开"
            )
            downloadType = DownloadType.Complete
            appInfo.isSystemApp = true
            showBadge.value = true
            appInfo.isShowBadge = true
          }

          override fun onError(
            errorCode: Int, errorMsg: String, exception: Throwable?
          ) {
            downloadType = DownloadType.Fail
            // 隐藏下载进度界面，显示对话框界面
            dialogState.updateState(customShow = true).customDialog.update(
              title = "异常提醒", content = errorMsg, confirmText = "重新下载"
            )
          }
        })
    } else {
      downloadType = DownloadType.Fail
      dialogState.updateState(customShow = true).customDialog.update(
        title = "异常提醒", content = "下载失败，找不到版本信息", confirmText = "重新下载"
      )
    }
  }, onCancel = { downloadType = DownloadType.Init })
  // 显示正在请求对话框
  // LoadingDialog(dialogState)
  // 显示下载进度对话框
  ProgressDialog(dialogState, onCancel = { downloadType = DownloadType.Init })
}

@Composable
fun AppInfoGridView(appInfoList: List<AppInfo>, onOpenApp: ((url: String) -> Unit)? = null) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(5),//GridCells.Adaptive(minSize = 60.dp), // 一行五个，或者指定大小
    contentPadding = PaddingValues(8.dp, 8.dp)
  ) {
    items(appInfoList) { item ->
      AppInfoView(appInfo = item, onOpenApp)
    }
  }
}

@Preview
@Composable
fun PreviewAppInfoView() {
  val list: ArrayList<AppInfo> = arrayListOf()
  for (i in 1..30) {
    list.add(
      AppInfo(
        version = "1.0.0",
        bfsAppId = "bsf-app-" + i,
        name = "name + " + i,
        icon = "",
        author = "",
        homepage = "",
        autoUpdate = AutoUpdateInfo(1, 1, "xxx")
      )
    )
  }
  AppInfoGridView(appInfoList = list)
}

private fun getDAppInfoUrl(appInfo: AppInfo): String {
  return FilesUtil.getDAppInfo(appInfo)?.let { it.enter.main } ?: ""
}

private fun hasNewVersion(cur: String, net: String): Boolean {
  var curArray = cur.split(".")
  var netArray = net.split(".")
  var minSize = when (curArray.size > netArray.size) {
    true -> netArray.size
    false -> curArray.size
  }
  for (i in (0..minSize)) {
    if (curArray[i].toInt() < netArray[i].toInt()) {
      return true
    }
  }
  if (curArray.size < netArray.size) return true
  return false
}
