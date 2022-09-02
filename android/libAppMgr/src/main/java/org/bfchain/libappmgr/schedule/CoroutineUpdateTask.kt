package org.bfchain.libappmgr.schedule

import android.util.Log
import kotlinx.coroutines.*
import org.bfchain.libappmgr.network.RetrofitManager
import org.bfchain.libappmgr.ui.main.MainRepository
import org.bfchain.libappmgr.utils.FilesUtil

class CoroutineUpdateTask : UpdateTask {

    private var scope: CoroutineScope? = null

    override fun scheduleUpdate(interval: Long) {
        cancle()
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            var count = 1 // 用于判断软件运行分钟数，主要为了和应用信息中的maxAge做校验，确认是否需要更新
            while (isActive) {
                Log.d("linge", "开始执行轮询->$count")
                try {
                    // Todo 开始执行轮询操作
                    FilesUtil.getAppInfoList().forEach { appInfo ->
                        Log.d("linge", "enter->${appInfo.bfsAppId}, ${appInfo.autoUpdate}")
                        if (count % appInfo.autoUpdate!!.maxAge == 0) {
                            Log.d("linge", "enter->${appInfo.bfsAppId} -> maxAge")
                            async {
                                delay(1000)
                                var savePath = FilesUtil.getAppUpdateDirectory(appInfo.bfsAppId)
                                MainRepository(RetrofitManager.apiService)
                                    .saveAppVersion(appInfo, savePath)
                                Log.d("linge", "finish->$appInfo")
                            }
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