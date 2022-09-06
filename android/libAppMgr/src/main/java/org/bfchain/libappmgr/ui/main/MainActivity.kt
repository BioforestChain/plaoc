package org.bfchain.libappmgr.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.data.PreferencesHelper
import org.bfchain.libappmgr.model.AppInfo
import org.bfchain.libappmgr.model.AutoUpdateInfo
import org.bfchain.libappmgr.schedule.CoroutineUpdateTask
import org.bfchain.libappmgr.ui.download.DownLoadView
import org.bfchain.libappmgr.ui.theme.AppMgrTheme
import org.bfchain.libappmgr.ui.view.AppInfoGridView
import org.bfchain.libappmgr.utils.FilesUtil

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch {

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
        // 判断是否是第一次加载软件，如果是，那么将assets中的remember-app拷贝到app目录下
        if (PreferencesHelper.isFirstIn()) {
            FilesUtil.copyAssetsToRememberAppDir()
            PreferencesHelper.saveFirstState(false)
        }
        // 拷贝完成后，通过app目录下的remember-app和system-app获取最新列表数据
        appInfoList.clear()
        appInfoList.addAll(FilesUtil.getAppInfoList())
    }
    AppInfoGridView(appInfoList = appInfoList)

    /*DownLoadView(
        appInfo = AppInfo(
            version = "xxx",
            bfsAppId = "bfs-app-1234567A",
            name = "name",
            icon = "null",
            author = "null",
            autoUpdate = AutoUpdateInfo(3, 3, "xxxx")
        )
    )*/
}

@Composable
fun Test() {
/*
    val downLoadViewModel: DownLoadViewModel = viewModel()
    downLoadViewModel.downloadAndSave(
        "/bfs-app-1234567A.zip",
        FilesUtil.getAppCacheDirectory() + File.separator + "zz.zip",
        onSuccess = {
            Log.d("MainActivity", "onSuccess->${it.name}")
        },
        onError = {
            Log.d("MainActivity", "onError->${it.printStackTrace()}")
        },
        onProcess = { currentLength, length, process ->
            Log.d("MainActivity", "onProcess->$currentLength, $length, $process")
        }
    )

    var viewModel: MainViewModel = viewModel()
    viewModel.getAppVersionAndSave(
        AppVersionPath,
        FilesUtil.getAppCacheDirectory() + File.separator + "aaa.json",
        onSuccess = {
            Log.d("MainActivity", "onSuccess->$it")
        },
        onError = {
            Log.d("MainActivity", "onError->$it")
        }
    )*/
}
