package org.bfchain.rust.plaoc

import android.app.Application
import android.content.Context
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class App : Application() {
    val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    companion object {
       var appContext: Context? = null
    }

  override fun onCreate() {
    super.onCreate()
    appContext = this
  }
}
