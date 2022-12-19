package info.bagen.rust.plaoc

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import info.bagen.libappmgr.di.libRepositoryModule
import info.bagen.libappmgr.di.libViewModelModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import info.bagen.libappmgr.utils.ClipboardUtil
import info.bagen.rust.plaoc.util.PlaocUtil
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class App : Application() {
  val executorService: ExecutorService = Executors.newFixedThreadPool(4)

  companion object {
    lateinit var appContext: Context

    var mainActivity:MainActivity? = null
  }

  override fun onCreate() {
    super.onCreate()
    appContext = this
    startKoin {
      androidLogger(Level.ERROR)
      androidContext(this@App)
      androidFileProperties()
      modules(
        libViewModelModule, libRepositoryModule
      )
    }
    PlaocUtil.addShortcut(this) // 添加桌面快捷方式
    // 启动deno runtime
    startDenoService()
    // 监听activity状态，获取当前哪个app在前台
    registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImp())
  }

  /** 启动Deno服务*/
  private fun startDenoService() {
    val deno = Intent(this, DenoService::class.java)
    startService(deno)
  }

  private class ActivityLifecycleCallbacksImp : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
      // android10中规定, 只有默认输入法(IME)或者是目前处于焦点的应用, 才能访问到剪贴板数据，所以要延迟到聚焦后
      activity.window.decorView.post {
        GlobalScope.launch { ClipboardUtil.readAndParsingClipboard(activity) }
      }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
  }
}

