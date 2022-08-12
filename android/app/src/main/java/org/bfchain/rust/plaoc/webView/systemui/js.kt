package org.bfchain.rust.plaoc.webView.systemui


import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bfchain.rust.plaoc.webView.jsutil.*
import org.bfchain.rust.plaoc.webView.systemui.js.VirtualKeyboardFFI
import org.bfchain.rust.plaoc.webkit.AdWebViewHook
import org.bfchain.rust.plaoc.webView.jsutil.DataString_From


private const val TAG = "SystemUiFFI"

class SystemUiFFI(
    private val activity: ComponentActivity,
    private val webView: WebView,
    private val hook: AdWebViewHook,
    private val jsUtil: JsUtil,
    private val systemUIState: SystemUIState,
) {
    //    @JavascriptInterface
    val virtualKeyboard =
        VirtualKeyboardFFI(systemUIState.virtualKeyboard.overlay, activity, webView)

    /**
     * @TODO 在未来，这里的disable与否，通过更加完善的声明来实现，比如可以声明多个rect
     */
    @JavascriptInterface
    fun disableTouchEvent() {
        hook.onTouchEvent = { false }
    }

    /**
     * 第一个参数是颜色HEX。第二个是图标颜色
     */
    @JavascriptInterface
    fun setStatusBarColor(
        colorHex: ColorInt,
        darkIcons: BoolInt,
    ) {
        systemUIState.statusBar.apply {
            color.value = Color(colorHex)
            isDarkIcons.value = darkIcons.toBooleanOrNull()
        }
    }

    @JavascriptInterface
    fun getStatusBarVisible(): Boolean {
        return systemUIState.statusBar.visible.value
    }

    /**
     * 设置0为透明
     */
    @JavascriptInterface
    fun toggleStatusBarVisible(visible: BoolInt) {
        systemUIState.statusBar.visible.value =
            visible.toBoolean { !systemUIState.statusBar.visible.value }
    }

    @JavascriptInterface
    fun getStatusBarOverlay(): Boolean {
        return systemUIState.statusBar.overlay.value
    }

    @JavascriptInterface
    fun toggleStatusBarOverlay(isOverlay: BoolInt) {
        systemUIState.statusBar.overlay.value =
            isOverlay.toBoolean { !systemUIState.statusBar.overlay.value }
        Log.i(TAG, "isOverlayStatusBar.value:${systemUIState.statusBar.overlay.value}")
    }


    @JavascriptInterface
    fun setNavigationBarColor(
        colorHex: ColorInt,
        darkIcons: BoolInt,
        isNavigationBarContrastEnforced: BoolInt
    ) {
        systemUIState.navigationBar.apply {
            color.value = Color(colorHex)
            isDarkIcons.value = darkIcons.toBooleanOrNull()
            isContrastEnforced.value = isNavigationBarContrastEnforced.toBooleanOrNull()
        }
    }

    @JavascriptInterface
    fun getNavigationBarVisible(): Boolean {
        return systemUIState.navigationBar.visible.value
    }

    @JavascriptInterface
    fun toggleNavigationBarVisible(visible: BoolInt) {
        systemUIState.navigationBar.visible.value =
            visible.toBoolean { !systemUIState.navigationBar.visible.value }
    }


    @JavascriptInterface
    fun getNavigationBarOverlay(): Boolean {
        return systemUIState.navigationBar.overlay.value
    }

    @JavascriptInterface
    fun toggleNavigationBarOverlay(isOverlay: BoolInt) {
        systemUIState.navigationBar.overlay.value =
            isOverlay.toBoolean { !systemUIState.navigationBar.overlay.value }
    }


    private val insetsCompat: WindowInsetsCompat by lazy {
        WindowInsetsCompat.toWindowInsetsCompat(
            activity.window.decorView.rootWindowInsets
        )
    }

    private fun Insets.toJson(): String {
        return """{"top":${top},"left":${left},"bottom":${bottom},"right":${right}}"""
    }

    @JavascriptInterface
    fun getInsetsTypeEnum(): String {
        return DataString_From(InsetsType())
    }

    class InsetsType {
        val FIRST = 1
        val STATUS_BARS = FIRST
        val NAVIGATION_BARS = 1 shl 1
        val CAPTION_BAR = 1 shl 2

        val IME = 1 shl 3

        val SYSTEM_GESTURES = 1 shl 4
        val MANDATORY_SYSTEM_GESTURES = 1 shl 5
        val TAPPABLE_ELEMENT = 1 shl 6

        val DISPLAY_CUTOUT = 1 shl 7

        val LAST = 1 shl 8
        val SIZE = 9
        val WINDOW_DECOR = LAST
    }

    @JavascriptInterface
    fun getInsetsRect(typeMask: Int, ignoreVisibility: BoolInt): String {
        if (ignoreVisibility.toBoolean()) {
            return insetsCompat.getInsetsIgnoringVisibility(typeMask).toJson()
        }
        return insetsCompat.getInsets(typeMask).toJson()
    }

    @JavascriptInterface
    fun showInsets(typeMask: Int) {
        return WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            .show(typeMask)
    }
}
