package org.bfchain.rust.plaoc.system

import android.app.Activity
import org.bfchain.rust.plaoc.ExportNative
import org.bfchain.rust.plaoc.MainActivity
import org.bfchain.rust.plaoc.callable_map
import org.bfchain.rust.plaoc.denoService
import org.bfchain.rust.plaoc.webView.network.initMetaData
import org.bfchain.rust.plaoc.webView.sendToJavaScript


/** 初始化系统函数*/
 fun initSystemFn(activity: MainActivity) {
  callable_map[ExportNative.OpenQrScanner] = { activity.openScannerActivity() }
  callable_map[ExportNative.BarcodeScanner] = { activity.openBarCodeScannerActivity() }
  callable_map[ExportNative.OpenDWebView] = {
    activity.openDWebViewActivity(it)
  }
  callable_map[ExportNative.InitMetaData] = {
    initMetaData(it)
  }
  callable_map[ExportNative.DenoRuntime] = {
    denoService.denoRuntime(it)
  }
  callable_map[ExportNative.EvalJsRuntime] =
    { sendToJavaScript(it) }
}
