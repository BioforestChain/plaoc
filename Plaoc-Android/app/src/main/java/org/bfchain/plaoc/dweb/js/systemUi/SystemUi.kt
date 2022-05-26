package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.gson.Gson
import org.bfchain.plaoc.dweb.js.util.*
import org.bfchain.plaoc.webkit.AdWebViewHook


private const val TAG = "SystemUiFFI"

class SystemUiFFI(
    val activity: ComponentActivity,
    val webView: WebView,
    val hook: AdWebViewHook,
    private val systemUiController: SystemUiController,
    private val jsUtil: JsUtil,
    private val isOverlayStatusBar: MutableState<Boolean>,
    private val isOverlayNavigationBar: MutableState<Boolean>
) {
//
//    init {
//        val devicePixelRatio = activity.resources.displayMetrics.density
//
//
//        ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView) { _, insets ->
//            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
//            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
//            Log.i(TAG, "imeHeight:$imeHeight")
//            GlobalScope.launch {
//                jsUtil.setJsValues(
//                    jsVirtualKeyboardNamespace,
//                    mapOf(
//                        "top" to JsValue(
//                            JsValueType.Number,
//                            (imeHeight / devicePixelRatio).toString()
//                        ),
//                        "state" to JsValue(JsValueType.String, if (imeVisible) "open" else "close")
//                    )
//                );
//            }
//            InjectVirtualKeyboardVars(insets)
//            insets
//        }
//
//
//        ViewCompat.setWindowInsetsAnimationCallback(
//            activity.window.decorView,
//            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
//                val view = activity.window.decorView
//                override fun onPrepare(animation: WindowInsetsAnimationCompat) {
//                    if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
//                        GlobalScope.launch {
//                            jsUtil.setJsValue(
//                                jsVirtualKeyboardNamespace,
//                                "state",
//                                JsValue(JsValueType.String, "opening"),
//                            );
//                        }
//                    }
//                    return super.onPrepare(animation)
//                }
//
//                override fun onStart(
//                    animation: WindowInsetsAnimationCompat,
//                    bounds: WindowInsetsAnimationCompat.BoundsCompat
//                ): WindowInsetsAnimationCompat.BoundsCompat {
//                    if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
//                        GlobalScope.launch {
//                            jsUtil.setJsValue(
//                                jsVirtualKeyboardNamespace,
//                                "state",
//                                JsValue(JsValueType.String, "open"),
//                            );
//                        }
//                    }
//                    return super.onStart(animation, bounds)
//                }
//
//                override fun onProgress(
//                    insets: WindowInsetsCompat,
//                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
//                ): WindowInsetsCompat {
//                    for (ani in runningAnimations) {
//                        // Find an IME animation.
//                        val imeAnimation = runningAnimations.find {
//                            it.typeMask and WindowInsetsCompat.Type.ime() != 0
//                        } ?: return insets
//
//                        InjectVirtualKeyboardVars(insets, imeAnimation.interpolatedFraction)
//                    }
//
//                    return insets
//                }
//
//                override fun onEnd(animation: WindowInsetsAnimationCompat) {
//                    if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
//                        GlobalScope.launch {
//                            jsUtil.setJsValue(
//                                jsVirtualKeyboardNamespace,
//                                "state",
//                                JsValue(JsValueType.String, "close")
//                            );
//                        }
//                    }
//                    return super.onEnd(animation)
//                }
//            }
//        )
//    }

    /**
     * @TODO 在未来，这里的disable与否，通过更加完善的声明来实现，比如可以声明多个rect
     */
    @JavascriptInterface
    fun disableTouchEvent() {
        hook.onTouchEvent = { false }
    }

    @JavascriptInterface
    fun setSystemBarsColor(
        colorHex: ColorInt,
        darkIcons: BoolInt,
        isNavigationBarContrastEnforced: BoolInt
    ) {
        val color = Color(colorHex)
        Log.i(TAG, "color: ${color.toArgb()}")
        activity.runOnUiThread {
            systemUiController.setSystemBarsColor(
                color = color,
                darkIcons = darkIcons.toBoolean { color.luminance() > 0.5f },
                isNavigationBarContrastEnforced = isNavigationBarContrastEnforced.toBoolean { true },
            )
        }
    }

    @JavascriptInterface
    fun setStatusBarColor(
        colorHex: ColorInt,
        darkIcons: BoolInt,
    ) {
        val color = Color(colorHex)
        Log.i(TAG, "color: ${color.toArgb()}")
        activity.runOnUiThread {
            systemUiController.setStatusBarColor(
                color = color,
                darkIcons = darkIcons.toBoolean { color.luminance() > 0.5 }
            )
        }
    }

    @JavascriptInterface
    fun setNavigationBarColor(
        colorHex: ColorInt,
        darkIcons: BoolInt,
        isNavigationBarContrastEnforced: BoolInt
    ) {
        val color = Color(colorHex)
        Log.i(TAG, "color: ${color.toArgb()}")
        activity.runOnUiThread {
            systemUiController.setNavigationBarColor(
                color = color,
                darkIcons = darkIcons.toBoolean { color.luminance() > 0.5f },
                navigationBarContrastEnforced = isNavigationBarContrastEnforced.toBoolean { true },
            )
        }
    }

    @JavascriptInterface
    fun getSystemBarsVisible(): Boolean {
        return systemUiController.isSystemBarsVisible
    }

    @JavascriptInterface
    fun toggleSystemBarsVisible(visible: BoolInt) {
        activity.runOnUiThread {
            systemUiController.isSystemBarsVisible =
                visible.toBoolean { !systemUiController.isSystemBarsVisible }
        }
    }

    @JavascriptInterface
    fun getStatusBarVisible(): Boolean {
        return systemUiController.isStatusBarVisible
    }

    @JavascriptInterface
    fun toggleStatusBarVisible(visible: BoolInt) {
        activity.runOnUiThread {
            systemUiController.isStatusBarVisible =
                visible.toBoolean { !systemUiController.isStatusBarVisible }
        }
    }

    @JavascriptInterface
    fun getStatusBarOverlay(): Boolean {
        return isOverlayStatusBar.value
    }

    @JavascriptInterface
    fun toggleStatusBarOverlay(isOverlay: BoolInt) {
        isOverlayStatusBar.value = isOverlay.toBoolean { !isOverlayStatusBar.value }
        Log.i(TAG, "isOverlayStatusBar.value:${isOverlayStatusBar.value}")
    }

    @JavascriptInterface
    fun getNavigationBarVisible(): Boolean {
        return systemUiController.isNavigationBarVisible
    }

    @JavascriptInterface
    fun toggleNavigationBarVisible(visible: BoolInt) {
        activity.runOnUiThread {
            systemUiController.isNavigationBarVisible =
                visible.toBoolean { !systemUiController.isNavigationBarVisible }
        }
    }


    @JavascriptInterface
    fun getNavigationBarOverlay(): Boolean {
        return isOverlayNavigationBar.value
    }

    @JavascriptInterface
    fun toggleNavigationBarOverlay(isOverlay: BoolInt) {
        isOverlayNavigationBar.value = isOverlay.toBoolean { !isOverlayNavigationBar.value }
        Log.i(TAG, "isOverlayNavigationBar.value:${isOverlayNavigationBar.value}")
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
