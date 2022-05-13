package org.bfchain.plaoc.plugin.systemUi

import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.accompanist.systemuicontroller.SystemUiController

private const val TAG = "SystemUiFfi"

class SystemUiFfi(
    val activity: ComponentActivity,
    private val systemUiController: SystemUiController
) {
    @JavascriptInterface
    fun setSystemBarsColor(
        colorHex: Int,
        darkIcons: Int,
        isNavigationBarContrastEnforced: Int
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

    private fun Int.toBoolean(elseDefault: () -> Boolean = { false }): Boolean {
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

    @JavascriptInterface
    fun setStatusBarColor(
        colorHex: Int,
        darkIcons: Int,
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
        colorHex: Int,
        darkIcons: Int,
        isNavigationBarContrastEnforced: Int
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
    fun toggleSystemBarsVisible(visible: Int) {
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
    fun toggleStatusBarVisible(visible: Int) {
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
    fun toggleDecorFitsSystemWindows(decorFitsSystemWindows: Int) {
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
    fun toggleNavigationBarVisible(visible: Int) {
        activity.runOnUiThread {
            systemUiController.isNavigationBarVisible =
                visible.toBoolean { !systemUiController.isNavigationBarVisible }
        }
    }

}