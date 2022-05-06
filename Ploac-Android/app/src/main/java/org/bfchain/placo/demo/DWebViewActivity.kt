package org.bfchain.placo.demo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.web.*
import org.bfchain.placo.demo.ui.theme.PlacoDemoTheme
import java.net.URLDecoder
import java.net.URLEncoder

private val TAG = "DWebViewActivity"

class DWebViewActivity : ComponentActivity() {
    companion object {
        val ALL = mutableListOf<DWebViewActivity>()
    }

    override fun onBackPressed() {
        Log.i(TAG, "parentActivityIntent:${this.parentActivityIntent}")

        if (this.parentActivityIntent == null) {
            super.onBackPressed()
        } else {
            this.startActivity(this.parentActivityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DWebViewActivity.ALL.add(this)

        android.webkit.WebView.setWebContentsDebuggingEnabled(true)
        Log.i(TAG, "onCreate")
        Log.i(TAG, "actionBar$actionBar")


//                val navController = rememberNavController()
        val activity = this;
        setContent {
            PlacoDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    NavFun(activity)
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
private fun NavFun(activity: ComponentActivity) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    Log.i(TAG, "navController.popBackStack()+${navController.popBackStack()}")

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController = navController, startDestination = "dweb/{url}") {
//            composable("home") {
//                Text(text = "HOME!!")
//            }
            composable(
                "dweb/{url}",
                arguments = listOf(
                    navArgument("url") {
                        type = NavType.StringType
                    }
                ),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "dweb://{url}"
                })
            ) { entry ->
                DWebView(rememberWebViewState(entry.arguments?.getString("url")
                    .let { it -> URLDecoder.decode(it, "UTF-8") }
                    ?: "file:///android_asset/demo.html"),
                    navController = navController,
                    activity = activity)
            }
        }
    }
}


@SuppressLint("JavascriptInterface")
@Composable
fun DWebView(
//    url: String,
    webviewState: WebViewState,
    navController: NavController,
    activity: ComponentActivity
) {
//    val webviewState = rememberWebViewState(url)
//    val context = LocalContext.current;
    WebView(
        state = webviewState,
        onCreated = { it ->
            it.settings.javaScriptEnabled = true;
            it.settings.setSupportMultipleWindows(true);
            it.settings.allowFileAccess = true;
            it.settings.javaScriptCanOpenWindowsAutomatically = true;
//            it.settings.pluginState = WebSettings.PluginState.ON;


            class JsObject {
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
            it.addJavascriptInterface(JsObject(), "my_nav")
        },
        client = remember {
            class MyWebViewClient : AccompanistWebViewClient() {
//                override fun shouldOverrideUrlLoading(
//                    view: WebView?,
//                    request: WebResourceRequest?
//                ): Boolean {
//                    request?.url?.let {
//                        view?.loadUrl(it.toString());
//                        return true;
//                    }
//
//                    return super.shouldOverrideUrlLoading(view, request)
//                }
            }

            var client = MyWebViewClient()
            client
        },
        chromeClient = remember {

            class MyWebChromeClient : AccompanistWebChromeClient() {
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
                    val href = view!!.handler.obtainMessage()
                    view!!.requestFocusNodeHref(href)
                    val url = href.data.getString("url")
//                    activity.togg
//                    href.recycle()
//                    webview.loadUrl(url)

                    openDWebWindow(activity = activity, url = url.toString())
//                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                    return true;
                }

            }

            var client = MyWebChromeClient()

            client
        }
    )
}

fun openDWebWindow(activity: ComponentActivity, url: String) {
    activity.applicationContext
//                        navController.navigate("demo-web/$to")
    var intent = Intent(activity.applicationContext, DWebViewActivity::class.java).also {
        it.data =
            Uri.parse("dweb://" + URLEncoder.encode(url, "UTF-8"));
//        it.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
//        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        it.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    activity.startActivity(intent)
}