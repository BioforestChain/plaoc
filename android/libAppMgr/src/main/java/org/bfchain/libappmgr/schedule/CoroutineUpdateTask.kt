package org.bfchain.libappmgr.schedule

import android.util.Log
import kotlinx.coroutines.*
import org.bfchain.libappmgr.network.base.AppVersionPath
import org.bfchain.libappmgr.ui.main.MainViewModel
import org.bfchain.libappmgr.utils.FilesUtil

class CoroutineUpdateTask : UpdateTask {

    private var scope: CoroutineScope? = null

    override fun scheduleUpdate(interval: Long) {
        cancle()
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            var count = 1 // 用于判断软件运行分钟数，主要为了和应用信息中的maxAge做校验，确认是否需要更新
            while (isActive) {
                Log.d("CoroutineUpdateTask", "scheduleUpdate->$count")
                try {
                    // Todo 开始执行轮询操作
                    FilesUtil.getAppInfoList().forEach { appInfo ->
                        if (count % appInfo.autoUpdate.maxAge == 0) {
                            var mainViewModel = MainViewModel()
                            //mainViewModel.getAppVersionAndSave(appInfo.autoUpdate.url, savePath)
                            mainViewModel.getAppVersionAndSave(appInfo)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                count++
                delay(interval)
            }
        }
        this.scope = scope
    }

    override fun cancle() {
        scope?.cancel()
        scope = null
    }
}