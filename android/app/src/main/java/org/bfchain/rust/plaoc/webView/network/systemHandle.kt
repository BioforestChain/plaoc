package org.bfchain.rust.plaoc.webView.network

import android.util.Log
import org.bfchain.rust.plaoc.ExportNativeUi
import org.bfchain.rust.plaoc.mapper
import org.bfchain.rust.plaoc.webView.jsutil.ColorInt
import org.bfchain.rust.plaoc.webView.systemui.SystemUiFFI

val call_ui_map = mutableMapOf<ExportNativeUi, (data: String) -> Any>()
private const val TAG = "systemHandle"
/**
 * 初始化操作ui的请求
 */
fun initUiFn(systemUiFFI:SystemUiFFI) {
  /**Navigation*/
  call_ui_map[ExportNativeUi.SetNavigationBarVisible] = {
    systemUiFFI.setNavigationBarVisible(it)
  }
  call_ui_map[ExportNativeUi.GetNavigationBarVisible] = {
    systemUiFFI.getNavigationBarVisible()
  }
  call_ui_map[ExportNativeUi.SetNavigationBarColor] = {
    val color = mapper.readValue(it, NavigationBarColor::class.java)
    systemUiFFI.setNavigationBarColor(color.colorHex,color.darkIcons,color.isNavigationBarContrastEnforced)
  }
  call_ui_map[ExportNativeUi.GetNavigationBarOverlay] = {
    systemUiFFI.getNavigationBarOverlay()
  }
  call_ui_map[ExportNativeUi.SetNavigationBarOverlay] = {
    systemUiFFI.setNavigationBarOverlay(it)
  }
  /**Status Bar*/
  call_ui_map[ExportNativeUi.GetStatusBarColor] = {
    systemUiFFI.getStatusBarColor()
  }
  call_ui_map[ExportNativeUi.SetStatusBarColor] = {
    val color = mapper.readValue(it, NavigationBarColor::class.java)
    systemUiFFI.setStatusBarColor(color.colorHex,color.darkIcons)
  }
  call_ui_map[ExportNativeUi.GetStatusBarIsDark] = {
    systemUiFFI.getStatusBarIsDark()
  }
  call_ui_map[ExportNativeUi.SetStatusBarOverlay] = {
    systemUiFFI.setStatusBarOverlay(it)
  }
  call_ui_map[ExportNativeUi.GetStatusBarOverlay] = {
    systemUiFFI.getStatusBarOverlay()
  }
  call_ui_map[ExportNativeUi.SetStatusBarVisible] = {
    systemUiFFI.setStatusBarVisible(it)
  }
  call_ui_map[ExportNativeUi.GetStatusBarVisible] = {
    systemUiFFI.getStatusBarVisible()
  }
}


data class NavigationBarColor(
  val colorHex: ColorInt = 16777215,
  val darkIcons: Boolean = true,
  val isNavigationBarContrastEnforced: Boolean = true
)



