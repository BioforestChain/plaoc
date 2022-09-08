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
}


data class NavigationBarColor(
  val colorHex: ColorInt = 16777215,
  val darkIcons: Boolean = true,
  val isNavigationBarContrastEnforced: Boolean = true
)



