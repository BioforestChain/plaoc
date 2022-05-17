package org.bfchain.plaoc.webkit

import android.content.Context
import android.view.MotionEvent
import android.webkit.WebView

class AdAndroidWebView(context: Context) : WebView(context) {
    var adWebViewHook: AdWebViewHook? = null
        get() = field
        set(value) {
            field = value
        }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return adWebViewHook?.onTouchEvent?.let { it(event) } ?: super.onTouchEvent(event)
    }
}

