package org.bfchain.libappmgr.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.data.PreferencesHelper
import org.bfchain.libappmgr.entity.AppInfo
import org.bfchain.libappmgr.entity.AppVersion
import org.bfchain.libappmgr.entity.AutoUpdateInfo
import org.bfchain.libappmgr.network.base.IApiResult
import org.bfchain.libappmgr.schedule.CoroutineUpdateTask
import org.bfchain.libappmgr.ui.download.DownLoadViewModel
import org.bfchain.libappmgr.ui.theme.AppMgrTheme
import org.bfchain.libappmgr.ui.view.AppInfoGridView
import org.bfchain.libappmgr.utils.FilesUtil
import java.io.File

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    GlobalScope.launch {
      // 判断是否是第一次加载软件，如果是，那么将assets中的remember-app拷贝到app目录下
      if (PreferencesHelper.isFirstIn()) {
        FilesUtil.copyAssetsToRecommendAppDir()
        PreferencesHelper.saveFirstState(false)
      }

      CoroutineUpdateTask().scheduleUpdate(1000 * 60)
    }

    setContent {
      AppMgrTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
        ) {
          Gretting(name = "Compose!!", this@MainActivity)
          // ProgressCircular( progressValue.value)
          // Test()
        }
      }
    }
  }
}

@SuppressLint(
  "UnrememberedMutableState", "FlowOperatorInvokedInComposition",
  "CoroutineCreationDuringComposition"
)
@Composable
fun Gretting(name: String, context: Context) {
  // Text(text = "Hello $name")
  var appInfoList: MutableList<AppInfo> = mutableStateListOf()
  LaunchedEffect(Unit) {
    // 拷贝完成后，通过app目录下的remember-app和system-app获取最新列表数据
    appInfoList.clear()
    appInfoList.addAll(FilesUtil.getAppInfoList())
  }
  AppInfoGridView(appInfoList = appInfoList, downModeDialog = false)
}
