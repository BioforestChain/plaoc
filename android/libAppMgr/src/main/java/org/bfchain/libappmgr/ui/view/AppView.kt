package org.bfchain.libappmgr.ui.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import org.bfchain.libappmgr.model.AppInfo
import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.model.AutoUpdateInfo
import org.bfchain.libappmgr.network.base.IApiResult
import org.bfchain.libappmgr.ui.download.DownLoadViewModel
import org.bfchain.libappmgr.ui.main.MainViewModel
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.libappmgr.utils.JsonUtil
import java.io.File

enum class DownloadType{Init, Process, Complete, Fail}

@SuppressLint("UnrememberedMutableState")
@Composable
fun AppInfoView(appInfo: AppInfo, onOpenApp: ((appInfo : AppInfo) -> Unit)?=null) {
    var coroutineScope = rememberCoroutineScope()
    var vmMain: MainViewModel = viewModel()
    var vmDown: DownLoadViewModel = viewModel()
    var downloadDialogState = AlertDialogState(confirmText = mutableStateOf("下载"))
    // var loadingDialogShow = remember { mutableStateOf(false) }
    var progressDialogShow = remember { mutableStateOf(false) }
    var progressValue = remember { mutableStateOf(0f) }
    var appVersion: AppVersion? = null
    var downloadType = DownloadType.Init
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
                    .clickable {
                        if (!appInfo.isSystemApp) {
                            downloadDialogState.changeState(showDialog = true)
                            var requestFinish = false
                            coroutineScope.launch {
                                var version =
                                    FilesUtil
                                        .getLastUpdateContent(appInfo.bfsAppId)
                                        ?.let {
                                            Log.d("AppView", "it->$it")
                                            JsonUtil.fromJson(AppVersion::class.java, it)
                                        }
                                if (!requestFinish) {
                                    appVersion = version
                                    downloadDialogState.changeState(
                                        title = appVersion?.releaseName ?: "更新版本",
                                        content = appVersion?.releaseNotes ?: "暂无版本信息"
                                    )
                                }
                            }
                            vmMain.getAppVersionAndSave(appInfo, object : IApiResult<AppVersion> {
                                override fun onSuccess(
                                    errorCode: Int,
                                    errorMsg: String,
                                    data: AppVersion
                                ) {
                                    requestFinish = true
                                    appVersion = data
                                    downloadDialogState.changeState(
                                        title = data.releaseName,
                                        content = data.releaseNotes
                                    )
                                }
                            })
                        }

                    }
            ) {
                Image(
                    bitmap = BitmapFactory.decodeFile(appInfo.iconPath)?.asImageBitmap()
                        ?: ImageBitmap.imageResource(id = R.drawable.ic_launcher),
                    contentDescription = "icon",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                if (appInfo.isShowBadge) {
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
            CustomAlertDialog(downloadDialogState, onConfirm = {
                Log.d("AppView", "下载 version->$appVersion")
                if (downloadType == DownloadType.Complete) {
                    Log.d("AppView", "点击打开")
                    downloadDialogState.changeState(showDialog = false)
                    downloadType = DownloadType.Init
                    onOpenApp?.let { onOpenApp(appInfo) }
                } else if (appVersion != null && appVersion!!.files.isNotEmpty()) {
                    // 调用下载操作
                    downloadDialogState.changeState(showDialog = false)
                    progressDialogShow.value = true
                    downloadType = DownloadType.Process
                    vmDown.downloadAndSave(
                        appVersion!!.files[0].url,
                        FilesUtil.getAppDownloadPath(),
                        object : IApiResult<Nothing> {
                            override fun downloadProgress(
                                current: Long,
                                total: Long,
                                progress: Float
                            ) {
                                // Log.d("AppView", "下载进展 -> $current, $total, $progress")
                                progressValue.value = progress
                                downloadType = DownloadType.Process
                            }

                            override fun downloadSuccess(file: File) {
                                // Log.d("AppView", "下载完成 -> ${file.absolutePath}")
                                FilesUtil.UnzipFile(
                                    file.absolutePath,
                                    desDirectory = FilesUtil.getAppUnzipPath(appInfo)
                                )
                                progressDialogShow.value = false // 隐藏下载进度界面，显示对话框界面
                                downloadDialogState.changeState(
                                    showDialog = true,
                                    content = "下载完成",
                                    confirmText = "打开"
                                )
                                downloadType = DownloadType.Complete
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
            }, onCancel = {downloadType = DownloadType.Init})
            // LoadingDialog(loadingDialogShow)
            ProgressDialog(progressDialogShow, progressValue, onCancel = {downloadType = DownloadType.Init})
        }
    }
}

@Composable
fun AppInfoGridView(appInfoList: List<AppInfo>, onOpenApp: ((appInfo : AppInfo) -> Unit)?=null) {
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
