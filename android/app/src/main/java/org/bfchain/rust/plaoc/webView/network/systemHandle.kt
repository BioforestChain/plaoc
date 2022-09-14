package org.bfchain.rust.plaoc.webView.network

import android.util.Log
import org.bfchain.rust.plaoc.ExportNativeUi
import org.bfchain.rust.plaoc.mapper
import org.bfchain.rust.plaoc.webView.bottombar.BottomBarFFI
import org.bfchain.rust.plaoc.webView.jsutil.ColorInt
import org.bfchain.rust.plaoc.webView.systemui.SystemUiFFI
import org.bfchain.rust.plaoc.webView.topbar.TopBarFFI

val call_ui_map = mutableMapOf<ExportNativeUi, (data: String) -> Any>()
private const val TAG = "systemHandle"
/**
 * 初始化操作ui的请求
 */
fun initSystemUiFn(systemUiFFI:SystemUiFFI) {
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
  /**keyboard*/
  call_ui_map[ExportNativeUi.GetKeyBoardSafeArea] = {
    systemUiFFI.virtualKeyboard.getSafeArea()
  }
  call_ui_map[ExportNativeUi.GetKeyBoardHeight] = {
    systemUiFFI.virtualKeyboard.getHeight()
  }
  call_ui_map[ExportNativeUi.GetKeyBoardOverlay] = {
    systemUiFFI.virtualKeyboard.getOverlay()
  }
  call_ui_map[ExportNativeUi.SetKeyBoardOverlay] = {
    systemUiFFI.virtualKeyboard.setOverlay(it)
  }
  call_ui_map[ExportNativeUi.ShowKeyBoard] = {
    systemUiFFI.virtualKeyboard.show()
  }
  call_ui_map[ExportNativeUi.HideKeyBoard] = {
    systemUiFFI.virtualKeyboard.hide()
  }
}

data class NavigationBarColor(
  val colorHex: ColorInt = 16777215,
  val darkIcons: Boolean = true,
  val isNavigationBarContrastEnforced: Boolean = true
)
/**
 * 初始化操作TopBar ui的请求
 */
fun initTopBarFn(topBarFFI: TopBarFFI) {
  call_ui_map[ExportNativeUi.TopBarNavigationBack] = {
    topBarFFI.topBarNavigationBack()
  }
  call_ui_map[ExportNativeUi.GetTopBarEnabled] = {
    topBarFFI.getTopBarEnabled()
  }
  call_ui_map[ExportNativeUi.SetTopBarEnabled] = {
    topBarFFI.setTopBarEnabled(it.toBoolean())
  }
  call_ui_map[ExportNativeUi.GetTopBarOverlay] = {
    topBarFFI.getTopBarOverlay()!!
  }
  call_ui_map[ExportNativeUi.SetTopBarOverlay] = {
    topBarFFI.setTopBarOverlay(it)!!
  }
  call_ui_map[ExportNativeUi.GetTopBarTitle] = {
    topBarFFI.getTopBarTitle()
  }
  call_ui_map[ExportNativeUi.SetTopBarTitle] = {
    topBarFFI.setTopBarTitle(it)
  }
  call_ui_map[ExportNativeUi.HasTopBarTitle] = {
    topBarFFI.hasTopBarTitle()
  }
  call_ui_map[ExportNativeUi.GetTopBarHeight] = {
    topBarFFI.getTopBarHeight()
  }
  call_ui_map[ExportNativeUi.GetTopBarActions] = {
    topBarFFI.getTopBarActions()
  }
  call_ui_map[ExportNativeUi.SetTopBarActions] = {
    topBarFFI.setTopBarActions(it)
  }
  call_ui_map[ExportNativeUi.GetTopBarBackgroundColor] = {
    topBarFFI.getTopBarBackgroundColor()
  }
  call_ui_map[ExportNativeUi.SetTopBarBackgroundColor] = {
    topBarFFI.setTopBarBackgroundColor(it.toInt())
  }
  call_ui_map[ExportNativeUi.GetTopBarForegroundColor] = {
    topBarFFI.getTopBarForegroundColor()
  }
  call_ui_map[ExportNativeUi.SetTopBarForegroundColor] = {
    topBarFFI.setTopBarForegroundColor(it.toInt())
  }
}

/**
 * 初始化bottomBar ui的请求
 */
fun initBottomFn(bottomBarFFI: BottomBarFFI) {
  call_ui_map[ExportNativeUi.GetBottomBarEnabled] = {
    bottomBarFFI.getEnabled()
  }
  call_ui_map[ExportNativeUi.SetBottomBarEnabled] = {
    bottomBarFFI.setEnabled(it)
  }
  call_ui_map[ExportNativeUi.GetBottomBarOverlay] = {
    bottomBarFFI.getOverlay()
  }
  call_ui_map[ExportNativeUi.SetBottomBarOverlay] = {
    bottomBarFFI.setOverlay(it)
  }
  call_ui_map[ExportNativeUi.GetBottomBarHeight] = {
    bottomBarFFI.getHeight()
  }
  call_ui_map[ExportNativeUi.SetBottomBarHeight] = {
    bottomBarFFI.setHeight(it)
  }
  call_ui_map[ExportNativeUi.GetBottomBarActions] = {
    bottomBarFFI.getActions()
  }
  call_ui_map[ExportNativeUi.SetBottomBarActions] = {
    bottomBarFFI.setActions(it)
  }
  call_ui_map[ExportNativeUi.GetBottomBarBackgroundColor] = {
    bottomBarFFI.getBackgroundColor()
  }
  call_ui_map[ExportNativeUi.SetBottomBarBackgroundColor] = {
    bottomBarFFI.setBackgroundColor(it.toInt())
  }
  call_ui_map[ExportNativeUi.GetBottomBarForegroundColor] = {
    bottomBarFFI.getForegroundColor()
  }
  call_ui_map[ExportNativeUi.SetBottomBarForegroundColor] = {
    bottomBarFFI.setForegroundColor(it.toInt())
  }
}


