package org.bfchain.rust.plaoc.system.deepLink

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.bfchain.rust.plaoc.App
import org.bfchain.rust.plaoc.MainActivity
import org.bfchain.rust.plaoc.WorkerNative
import org.bfchain.rust.plaoc.createWorker
import java.io.File

class DWebReceiver : BroadcastReceiver() {
  companion object {
    const val ACTION_OPEN_DWEB = "action.plaoc.open_dwebview"
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    intent?.let { it ->
      if (ACTION_OPEN_DWEB == it.action) {
        val appName = it.getStringExtra("AppName")
        val url = it.getStringExtra("URL")
        val callUrl = "${App.appContext.dataDir.absolutePath}/system-app/$appName/boot/$url"
        if (File(callUrl).exists()) {
          createWorker(WorkerNative.valueOf("DenoRuntime"), callUrl)
        } else {
          context?.let { cc -> cc.startActivity(Intent(cc, MainActivity::class.java)) }
          Toast.makeText(context, "请安装应用<$appName>", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}
