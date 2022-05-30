package org.bfchain.plaoc.dweb.navigator

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import org.bfchain.plaoc.dweb.openDWebWindow

private const val TAG = "js/MyNav"

class NavigatorFFI(
    val webView: WebView,
    val activity: ComponentActivity,
    val navController: NavController
) {
    @JavascriptInterface
    fun test(to: String): String {
        return to
    }

    @JavascriptInterface
    fun navigate(to: String) {
        activity.runOnUiThread() {
            navController.navigate(to)
        }
    }

    @JavascriptInterface
    fun navigateWeb(to: String) {
        activity.runOnUiThread() {
            openDWebWindow(activity = activity, url = to)

            Log.i(TAG, "startActivity!!!")
        }
    }

    @JavascriptInterface
    fun jsFunction(a: Int, b: Int) {
        Log.e(TAG, "call jsFunction in java.fail~~~")
    }

    @JavascriptInterface
    fun testCallJs() {
//                    jsFunction(1, 2)
        activity.runOnUiThread {
            webView.evaluateJavascript("my_nav.jsFunction2(2,3)") { result ->
                Log.i(TAG, "eval jsFunction ${result.toFloat()}")
            };
        }
    }

    @JavascriptInterface
    fun disableBack() {

    }

    @JavascriptInterface
    fun back() {
        activity.runOnUiThread() {
            activity.onBackPressed()
        }
//                    activity.runOnUiThread() {
//                        Log.i(TAG, "activity.parentActivityIntent:${activity.parentActivityIntent}")
//                        if (activity.parentActivityIntent == null) {
//                            val latest = DWebViewActivity.ALL.removeLastOrNull()
//                            if (latest != null) {
//                                val parent = DWebViewActivity.ALL.lastOrNull()
//                                Log.i(TAG, "DWebViewActivity.ALL.lastOrNull:$parent")
//                                if (parent != null) {
//                                    Log.i(TAG, "parent.intent:${parent.intent}")
//                                    parent.intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                                    parent.intent.removeFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
//                                    parent.intent.removeFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
//                                    parent.intent.removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    activity.startActivity(parent.intent);
//                                    return@runOnUiThread
//                                }
//                            }
//                            activity.onBackPressed()
//                        } else {
//                            activity.startActivity(activity.parentActivityIntent)
//                        }
//                    }
    }
}