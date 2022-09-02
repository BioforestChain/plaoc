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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.data.PreferencesHelper
import org.bfchain.libappmgr.model.AppInfo
import org.bfchain.libappmgr.network.RetrofitManager
import org.bfchain.libappmgr.schedule.CoroutineUpdateTask
import org.bfchain.libappmgr.ui.theme.AppMgrTheme
import org.bfchain.libappmgr.ui.view.AppInfoGridView
import org.bfchain.libappmgr.utils.FilesUtil

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var progressValue = mutableStateOf(0f)
        GlobalScope.launch {
            /*DownloadViewModel(DownloadRepository(RetrofitManager.downLoadService))
                .downloadApp("http://rhhh27ht1.sabkt.gdipper.com/week2__CountdownTimer-main.zip",
                    FilesUtil.getAppCacheDirectory() + File.separator + "cache.zip",
                    onError = { e -> Log.d("lin.huang", "err-> $e") },
                    onProcess = { size, cur, progress ->
                        Log.d("lin.huang", "size->$size, cur->$cur, progress->$progress")
                        progressValue.value = progress
                    },
                    onSuccess = { Log.d("lin.huang", "sucess") })*/
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
                    //ProgressCircular( progressValue.value)
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
        if (PreferencesHelper.isFirstIn(context)) {
            FilesUtil.copyAssetsToRememberAppDir()
            PreferencesHelper.saveFirstState(false)
        }

        // 拷贝完成后，通过app目录下的remember-app和system-app获取最新列表数据
        appInfoList.clear()
        appInfoList.addAll(FilesUtil.getAppInfoList())

        /*var viewModel: MainViewModel = MainViewModel(MainRepository(RetrofitManager.apiService))
        appInfoList.forEach {
            viewModel.showAndSaveAppVersion(
                it,
                FilesUtil.getAppUpdateDirectory(it.bfsAppId)
            )
                .catch { Log.d("lin.huang", "异常") }
                .collectLatest { appVersion ->
                    Log.d("lin.huang", "正常返回")
                    Log.d("lin.huang", "articleData=$appVersion")

                    var lastfile = FilesUtil.getLastUpdateFile(it.bfsAppId)
                    Log.d("lin.huang", "lastfile=${lastfile?.absolutePath}")
                }
        }*/

    }

    AppInfoGridView(appInfoList = appInfoList)
}
