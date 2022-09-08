package org.bfchain.libappmgr.ui.download

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import org.bfchain.libappmgr.model.AppInfo
import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.network.base.IApiResult
import org.bfchain.libappmgr.ui.main.MainViewModel
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.libappmgr.utils.JsonUtil

@Composable
fun DownLoadView(appInfo: AppInfo) {
    // 点击界面后，获取本地最新的版本信息
    var appVersion = JsonUtil.fromJson(
        AppVersion::class.java,
        value = FilesUtil.getLastUpdateContent(appInfo.bfsAppId) ?: ""
    )
    Log.d("DownLoadView", "DownLoadView->$appVersion")

    // 后台异步请求最新的版本信息，如果当前版本跟实际显示不一样，根据最新版本内容显示
    var mainViewModel: MainViewModel = viewModel()
    mainViewModel.getAppVersionAndSave(appInfo, apiResult = object : IApiResult<AppVersion> {
        override fun onPrepare() {
            // 显示默认界面
        }

        override fun onSuccess(errorCode: Int, errorMsg: String, data: AppVersion) {
            // 显示加载后的界面
        }
    })

    // 点击下载按钮后，进行下载操作，同时显示下载进度界面
    // 下载完成后，同步进行安装（解压）操作。最后更新界面
}