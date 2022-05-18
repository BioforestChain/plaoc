package org.bfchain.plaoc.dweb

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.graphics.Color
import android.os.Message
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
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
    state: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
) {

    val systemUiController = rememberSystemUiController()
    val isOverlayStatusBar = remember { mutableStateOf(false) }
    val isOverlayNavigationBar = remember { mutableStateOf(false) }
    val insetsCompat = remember(activity.window.decorView.rootWindowInsets) {
        WindowInsetsCompat.toWindowInsetsCompat(
            activity.window.decorView.rootWindowInsets
        )
    }

//    Column() {
//        Button(
//            onClick = { isOverlayStatusBar.value = !isOverlayStatusBar.value },
//            modifier = Modifier.statusBarsPadding()
//        ) {
//            Text(text = "toggleOverlayStatusBar:${isOverlayStatusBar.value}")
//        }
//        Button(onClick = { isOverlayNavigationBar.value = !isOverlayNavigationBar.value }) {
//            Text(text = "toggleOverlayNavigationBar:${isOverlayNavigationBar.value}")
//        }
    AdWebView(
        state = state,
        onCreated = { webView ->
            // 将webView的背景默认设置为透明。不通过systemUi的api提供这个功能，一些手机上动态地修改webView背景颜色，在黑夜模式下，会有问题
            webView.setBackgroundColor(Color.TRANSPARENT);

            val jsUtil = JsUtil(activity, evaluateJavascript = { code, callback ->
                webView.evaluateJavascript(
                    code,
                    callback
                )
            });
            val navigatorFFI = NavigatorFFI(webView, activity, navController)
            webView.addJavascriptInterface(navigatorFFI, "my_nav")


            val systemUiFFI = SystemUiFFI(
                activity,
                webView,
                systemUiController,
                jsUtil,
                isOverlayStatusBar,
                isOverlayNavigationBar
            )
            webView.addJavascriptInterface(systemUiFFI, "system_ui")
        },
        chromeClient = remember {
            class MyWebChromeClient : AdWebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title);
                    Log.i(TAG, "TITLE CHANGED!!!! $title")

                    activity.runOnUiThread {
                        activity.setTaskDescription(
                            ActivityManager.TaskDescription(
                                title ?: ""
                            )
                        )
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
        modifier = modifier.let { m ->
            var typeMask: Int = 0;
            if (!isOverlayStatusBar.value) {
                typeMask = typeMask or WindowInsetsCompat.Type.statusBars()
            }
            if (!isOverlayNavigationBar.value) {
                typeMask = typeMask or WindowInsetsCompat.Type.navigationBars()
            }
            val insets = insetsCompat.getInsets(typeMask)
            with(LocalDensity.current) {
                modifier.padding(top = insets.top.toDp(), bottom = insets.bottom.toDp())
            }
        },
    )
//    }
}

