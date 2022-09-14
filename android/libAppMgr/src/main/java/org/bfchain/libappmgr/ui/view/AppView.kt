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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.R
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
  var downloadDialogState = AlertDialogState(confirmText = mutableStateOf("下载"))
  // var loadingDialogShow = remember { mutableStateOf(false) }
  var progressDialogShow = remember { mutableStateOf(false) }
  var progressValue = remember { mutableStateOf(0f) }
  var showBadge = remember { mutableStateOf(appInfo.isShowBadge) }
  var appVersion: AppVersion? = null
  var downloadType = DownloadType.Init

  // 显示界面图标
  AppInfoView(showBadge = showBadge, appInfo = appInfo, onClick = {
    Log.d("AppView", "判断是打开app还是下载-> ${appInfo.isSystemApp}")
    if (!appInfo.isSystemApp) {
      downloadDialogState.changeState(showDialog = true)
      var requestFinish = false
      coroutineScope.launch {
        var version = FilesUtil.getLastUpdateContent(appInfo)?.let {
          Log.d("AppView", "it->$it")
          JsonUtil.fromJson(AppVersion::class.java, it)
        }
        if (!requestFinish) {
          appVersion = version
          downloadDialogState.changeStateNewVersion(appVersion = appVersion)
        }
      }
      vmMain.getAppVersionAndSave(appInfo, object : IApiResult<AppVersion> {
        override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
          requestFinish = true
          appVersion = data
          downloadDialogState.changeStateNewVersion(appVersion = appVersion)
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
      }
      vmMain.getAppVersion(appInfo, object : IApiResult<AppVersion> {
        override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
          // 请求成功后，比对获取的版本号和当前版本号
          if (dappInfo == null || hasNewVersion(dappInfo.version, data.version)) {
            appVersion = data
            downloadDialogState.changeStateNewVersion(showDialog = true, appVersion = appVersion)
          }
        }
      })
    }
  })
  // 显示提示对话框
  CustomAlertDialog(downloadDialogState, onConfirm = {
    if (downloadType == DownloadType.Fail) {
      downloadDialogState.changeState(showDialog = false)
      downloadType = DownloadType.Init
    } else if (downloadType == DownloadType.Complete) {
      downloadDialogState.changeState(showDialog = false)
      downloadType = DownloadType.Init
      var url = getDAppInfoUrl(appInfo)
      Toast.makeText(
        AppContextUtil.sInstance, "直接打开应用-->${appInfo.name} url->$url", Toast.LENGTH_SHORT
      ).show()
      onOpenApp?.let { onOpenApp(url) }
    } else if (appVersion != null && appVersion!!.files.isNotEmpty()) {
      // 调用下载操作
      downloadDialogState.changeState(showDialog = false)
      progressDialogShow.value = true
      downloadType = DownloadType.Process
      vmDown.downloadAndSave(
        appVersion!!.files[0].url,
        FilesUtil.getAppDownloadPath(),
        object : IApiResult<Nothing> {
          override fun downloadProgress(current: Long, total: Long, progress: Float) {
            progressValue.value = progress
            downloadType = DownloadType.Process
          }

          override fun downloadSuccess(file: File) {
            FilesUtil.UnzipFile(
              file.absolutePath,
              desDirectory = FilesUtil.getAppUnzipPath(appInfo)
            )
            file.deleteOnExit() // 解压后，删除压缩包
            progressDialogShow.value = false // 隐藏下载进度界面，显示对话框界面
            downloadDialogState.changeState(
              showDialog = true,
              content = "下载完成",
              confirmText = "打开"
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
            progressDialogShow.value = false // 隐藏下载进度界面，显示对话框界面
            downloadDialogState.changeState(
              showDialog = true,
              title = "异常提醒",
              content = errorMsg,
              confirmText = "重新下载"
            )
          }
        })
    } else {
      downloadType = DownloadType.Fail
      downloadDialogState.changeState(
        showDialog = true,
        title = "异常提醒",
        content = "获取版本信息失败",
        confirmText = "关闭"
      )
    }
  }, onCancel = { downloadType = DownloadType.Init })
  // 显示正在请求对话框
  // LoadingDialog(loadingDialogShow)
  // 显示下载进度对话框
  ProgressDialog(
    progressDialogShow,
    progressValue,
    onCancel = { downloadType = DownloadType.Init })
}

@Composable
fun AppInfoGridView(appInfoList: List<AppInfo>, onOpenApp: ((url: String) -> Unit)? = null) {
  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 60.dp),
    contentPadding = PaddingValues(16.dp, 8.dp)
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
