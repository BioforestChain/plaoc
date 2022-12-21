package info.bagen.rust.plaoc.webView.api

import android.content.Intent
import android.webkit.JavascriptInterface
import info.bagen.rust.plaoc.App
import info.bagen.rust.plaoc.broadcast.BFSBroadcastAction

class BFSApi {

  @JavascriptInterface
  fun BFSInstallApp(path: String) {
    val intent = Intent(BFSBroadcastAction.BFSInstallApp.action).apply {
      putExtra("path", path)
    }
    App.appContext.sendBroadcast(intent)
  }
}
