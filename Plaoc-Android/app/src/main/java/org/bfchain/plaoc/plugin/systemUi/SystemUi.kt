package org.bfchain.plaoc.plugin.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import com.google.accompanist.systemuicontroller.SystemUiController

private const val TAG = "SystemUiFfi"

class SystemUiFfi(
    val activity: ComponentActivity,
    private val systemUiController: SystemUiController
) {
    @JavascriptInterface
    fun setSystemBarsColor(
        colorHex: Int,
        darkIcons: Boolean?,
        isNavigationBarContrastEnforced: Boolean?
    ) {
        val color = Color(colorHex)
        Log.i(TAG, "color: ${color.toArgb()}")
        activity.runOnUiThread {
            systemUiController.setSystemBarsColor(
                color = color,
                darkIcons = darkIcons ?: (color.luminance() > 0.5f),
                isNavigationBarContrastEnforced = isNavigationBarContrastEnforced ?: true,
            )
        }
    }

    @JavascriptInterface
    fun setStatusBarColor(
        colorHex: Int,
        darkIcons: Boolean?,
    ) {
        val color = Color(colorHex)
        Log.i(TAG, "color: ${color.toArgb()}")
        activity.runOnUiThread {
            systemUiController.setStatusBarColor(
                color = color,
                darkIcons = darkIcons ?: (color.luminance() > 0.5f),
            )
        }
    }

    @JavascriptInterface
    fun setNavigationBarColor(
        colorHex: Int,
        darkIcons: Boolean?,
        isNavigationBarContrastEnforced: Boolean?
    ) {
        val color = Color(colorHex)
        Log.i(TAG, "color: ${color.toArgb()}")
        activity.runOnUiThread {
            systemUiController.setNavigationBarColor(
                color = color,
                darkIcons = darkIcons ?: (color.luminance() > 0.5f),
                navigationBarContrastEnforced = isNavigationBarContrastEnforced ?: true,
            )
        }
    }

    @JavascriptInterface
    fun toggleSystemBarsVisible(visible: Boolean = !systemUiController.isSystemBarsVisible) {
        activity.runOnUiThread {
            systemUiController.isSystemBarsVisible = visible
        }
    }

    @JavascriptInterface
    fun toggleStatusBarVisible(visible: Boolean = !systemUiController.isStatusBarVisible) {
        activity.runOnUiThread {
            systemUiController.isStatusBarVisible = visible
        }
    }
    @JavascriptInterface
    fun toggleNavigationBarVisible(visible: Boolean = !systemUiController.isNavigationBarVisible) {
        activity.runOnUiThread {
            systemUiController.isNavigationBarVisible = visible
        }
    }

}