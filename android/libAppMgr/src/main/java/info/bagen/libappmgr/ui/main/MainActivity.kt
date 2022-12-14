package info.bagen.libappmgr.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import info.bagen.libappmgr.data.PreferencesHelper
import info.bagen.libappmgr.schedule.CoroutineUpdateTask
import info.bagen.libappmgr.ui.app.AppViewIntent
import info.bagen.libappmgr.ui.app.AppViewModel
import info.bagen.libappmgr.ui.theme.AppMgrTheme
import info.bagen.libappmgr.utils.FilesUtil

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppMgrTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
        ) {
          Home()
        }
      }
    }
  }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Home(onOpenDWebview: ((appId: String, url: String) -> Unit)? = null) {
  val appViewModel = viewModel() as AppViewModel
  val mainViewModel = viewModel() as MainViewModel
  LaunchedEffect(Unit) {
    if (PreferencesHelper.isFirstIn()) {
      FilesUtil.copyAssetsToRecommendAppDir()
      PreferencesHelper.saveFirstState(false)
    }
    // 拷贝完成后，通过app目录下的remember-app和system-app获取最新列表数据
    // AppContextUtil.appInfoList.clear()
    // AppContextUtil.appInfoList.addAll(FilesUtil.getAppInfoList())
    appViewModel.handleIntent(AppViewIntent.LoadAppInfoList) // 新增的加载
    CoroutineUpdateTask().scheduleUpdate(1000 * 60) // 轮询执行
  }
  //AppInfoGridView(appInfoList = AppContextUtil.appInfoList, downModeDialog = true, onOpenApp = onOpenDWebview)
  //AppInfoGridView(appViewModel, onOpenApp = onOpenDWebview)
  MainView(mainViewModel, appViewModel, onOpenDWebview)
}
