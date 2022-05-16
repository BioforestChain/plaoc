package org.bfchain.plaoc.plugin.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bfchain.plaoc.plugin.util.JsUtil
import org.bfchain.plaoc.plugin.util.JsValue
import org.bfchain.plaoc.plugin.util.JsValueType


private const val TAG = "SystemUiFfi"

class SystemUiFfi(
    val activity: ComponentActivity,
    val webView: WebView,
    private val systemUiController: SystemUiController,
    private val jsUtil: JsUtil
) {
    init {
        val jsVirtualKeyboardNamespace = "virtualKeyboard"
        val devicePixelRatio = activity.resources.displayMetrics.density

        ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            Log.i(TAG, "imeHeight:$imeHeight")
            GlobalScope.launch {
                jsUtil.setJsValue(
                    jsVirtualKeyboardNamespace,
                    "top",
                    JsValue(JsValueType.Number, (imeHeight / devicePixelRatio).toString()),
                );
            }
            insets
        }


        ViewCompat.setWindowInsetsAnimationCallback(
            activity.window.decorView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                val cssNamespace = "--virtual-keyboard"
                val view = activity.window.decorView
                override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                    if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
                        GlobalScope.launch {
                            jsUtil.setJsValue(
                                jsVirtualKeyboardNamespace,
                                "state",
                                JsValue(JsValueType.String, "prepare"),
                            );
                        }
                    }
                    return super.onPrepare(animation)
                }

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
                        GlobalScope.launch {
                            jsUtil.setJsValue(
                                jsVirtualKeyboardNamespace,
                                "state",
                                JsValue(JsValueType.String, "start"),
                            );
                        }
                    }
                    return super.onStart(animation, bounds)
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    for (ani in runningAnimations) {
                        // Find an IME animation.
                        val imeAnimation = runningAnimations.find {
                            it.typeMask and WindowInsetsCompat.Type.ime() != 0
                        } ?: return insets

                        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                        Log.i(TAG, "onProgress:$imeInsets")
                        GlobalScope.launch {
                            val x = imeInsets.left / devicePixelRatio;
                            val y = imeInsets.top / devicePixelRatio;
                            val width = (imeInsets.right - imeInsets.left) / devicePixelRatio;
                            val height = (imeInsets.bottom - imeInsets.top) / devicePixelRatio;

                            jsUtil.setJsValues(
                                jsVirtualKeyboardNamespace, mapOf(
                                    "state" to JsValue(JsValueType.String, "progress"),
                                    "x" to JsValue(JsValueType.Number, x.toString()),
                                    "y" to JsValue(JsValueType.Number, y.toString()),
                                    "width" to JsValue(JsValueType.Number, width.toString()),
                                    "height" to JsValue(JsValueType.Number, height.toString()),
                                    "animationProgress" to JsValue(
                                        JsValueType.Number,
                                        imeAnimation.interpolatedFraction.toString()
                                    ),
                                )
                            )
                            jsUtil.setCssVars(
                                "html",
                                mapOf(
                                    "$cssNamespace-x" to "${x}px",
                                    "$cssNamespace-y" to "${y}px",
                                    "$cssNamespace-width" to "${width}px",
                                    "$cssNamespace-height" to "${height}px",
                                    "$cssNamespace-animation-progress" to "${imeAnimation.interpolatedFraction}",
                                )
                            )
                        }
                    }

                    return insets
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
                        GlobalScope.launch {
                            jsUtil.setJsValue(
                                jsVirtualKeyboardNamespace,
                                "state",
                                JsValue(JsValueType.String, "end")
                            );
                        }
                    }
                    return super.onEnd(animation)
                }
            }
        )
    }

    @JavascriptInterface
    fun setBackgroundColor(colorHex: ColorInt) {
        webView.setBackgroundColor(colorHex)
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

    private var isDecorFitsSystemWindows: Boolean = true

    @JavascriptInterface
    fun getDecorFitsSystemWindows(): Boolean {
        return isDecorFitsSystemWindows
    }


    @JavascriptInterface
    fun toggleDecorFitsSystemWindows(decorFitsSystemWindows: BoolInt) {
        activity.runOnUiThread {
            isDecorFitsSystemWindows =
                decorFitsSystemWindows.toBoolean { !isDecorFitsSystemWindows }
            WindowCompat.setDecorFitsSystemWindows(
                activity.window,
                isDecorFitsSystemWindows
            )
        }
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
        val gson = Gson()
        return gson.toJson(InsetsType())
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

typealias ColorInt = Int

typealias BoolInt = Int

private fun BoolInt.toBoolean(elseDefault: () -> Boolean = { false }): Boolean {
    return when {
        this > 0 -> {
            true
        }
        this < 0 -> {
            false
        }
        else -> {
            elseDefault()
        }
    }
}
