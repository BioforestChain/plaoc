package org.bfchain.plaoc.dweb

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.bfchain.plaoc.dweb.js.systemUi.SystemUiFFI
import org.bfchain.plaoc.dweb.js.navigator.NavigatorFFI
import org.bfchain.plaoc.dweb.js.util.JsUtil
import org.bfchain.plaoc.webkit.AdWebChromeClient
import org.bfchain.plaoc.webkit.AdWebView
import org.bfchain.plaoc.webkit.AdWebViewState


private const val TAG = "DWebView"

@SuppressLint("JavascriptInterface")
@Composable
fun DWebView(
    webViewState: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
) {
    val systemUiController = rememberSystemUiController()


    AdWebView(
        state = webViewState,
        onCreated = { webView ->
            val jsUtil = JsUtil(activity, evaluateJavascript = { code, callback ->
                webView.evaluateJavascript(
                    code,
                    callback
                )
            });
            val navigatorFFI = NavigatorFFI(webView, activity, navController)
            webView.addJavascriptInterface(navigatorFFI, "my_nav")


            val systemUiFFI = SystemUiFFI(activity, webView, systemUiController, jsUtil)
            webView.addJavascriptInterface(systemUiFFI, "system_ui")
        },
        chromeClient = remember {
            class MyWebChromeClient : AdWebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title);
                    Log.i(TAG, "TITLE CHANGED!!!! $title")

                    activity.runOnUiThread {
                        activity.setTaskDescription(ActivityManager.TaskDescription(title ?: ""))
                    }
                }

                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    if (view != null) {
                        val href = view.handler.obtainMessage()
                        view.requestFocusNodeHref(href)
                        val url = href.data.getString("url")
                        if (url != null) {
                            openDWebWindow(activity = activity, url = url)
                            return true;
                        }
                    }
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)

                }

            }
            MyWebChromeClient()
        },
        modifier = modifier,
    )
}
