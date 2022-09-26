package org.bfchain.libappmgr.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import org.bfchain.libappmgr.data.PreferencesHelper
import org.bfchain.libappmgr.entity.AppInfo
import org.bfchain.libappmgr.schedule.CoroutineUpdateTask
import org.bfchain.libappmgr.ui.theme.AppMgrTheme
import org.bfchain.libappmgr.ui.view.AppInfoGridView
import org.bfchain.libappmgr.utils.FilesUtil

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    /*GlobalScope.launch {
      // 判断是否是第一次加载软件，如果是，那么将assets中的remember-app拷贝到app目录下
      if (PreferencesHelper.isFirstIn()) {
        FilesUtil.copyAssetsToRecommendAppDir()
        PreferencesHelper.saveFirstState(false)
      }

      CoroutineUpdateTask().scheduleUpdate(1000 * 60)
    }*/

    setContent {
      AppMgrTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
        ) {
          Home()
          //Gretting(name = "Compose!!")
        }
      }
    }
  }
}

@Composable
fun Gretting(name: String) {
  Text(text = "Hello $name")
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Home() {
  var appInfoList: MutableList<AppInfo> = mutableStateListOf()
  LaunchedEffect(Unit) {
    if (PreferencesHelper.isFirstIn()) {
      FilesUtil.copyAssetsToRecommendAppDir()
      PreferencesHelper.saveFirstState(false)
    }
    // 拷贝完成后，通过app目录下的remember-app和system-app获取最新列表数据
    appInfoList.clear()
    appInfoList.addAll(FilesUtil.getAppInfoList())
    CoroutineUpdateTask().scheduleUpdate(1000 * 60) // 轮询执行
  }
  AppInfoGridView(appInfoList = appInfoList, downModeDialog = false)
}
