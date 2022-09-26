package org.bfchain.rust.plaoc

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class App : Application() {
    val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    companion object {
       lateinit var appContext: Context
    }

  override fun onCreate() {
    super.onCreate()
    appContext = this
    startDenoService()
  }

  /** 启动Deno服务*/
  private fun startDenoService() {
    val deno = Intent(this, DenoService::class.java)
    startService(deno)
  }
}
