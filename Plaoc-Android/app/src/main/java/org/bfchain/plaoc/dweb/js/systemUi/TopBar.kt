package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState

private const val TAG = "TopBarFFI"

class TopBarFFI(
    val overlay: MutableState<Boolean>,
    val title: MutableState<String?>,
    val height: MutableState<Float>
) {
    @JavascriptInterface
    fun getOverlay(): Boolean {
        return overlay.value
    }

    @JavascriptInterface
    fun toggleOverlay(isOverlay: BoolInt): Boolean {
        overlay.value = isOverlay.toBoolean { !overlay.value }
        Log.i(TAG, "toggleOverlay:${overlay.value}")
        return overlay.value
    }

    @JavascriptInterface
    fun getTitle(): String {
        return title.value ?: ""
    }

    @JavascriptInterface
    fun hasTitle(): Boolean {
        return title.value != null
    }

    @JavascriptInterface
    fun setTitle(str: String) {
        title.value = str
    }

    @JavascriptInterface
    fun getHeight(): Float {
        return height.value
    }
}