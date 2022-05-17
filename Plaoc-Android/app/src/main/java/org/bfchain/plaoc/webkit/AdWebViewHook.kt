package org.bfchain.plaoc.webkit

import android.view.MotionEvent

class AdWebViewHook {
    var onTouchEvent: ((event: MotionEvent?) -> Boolean)? = null
//    var onOpenWindow: ((event: MotionEvent?) -> Boolean)? = null
}