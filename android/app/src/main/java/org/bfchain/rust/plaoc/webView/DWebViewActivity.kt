package org.bfchain.rust.plaoc.webView


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.work.WorkManager
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.king.mlkit.vision.camera.util.LogUtils
import org.bfchain.rust.plaoc.App
import org.bfchain.rust.plaoc.MainActivity
import org.bfchain.rust.plaoc.ui.theme.RustApplicationTheme
import org.bfchain.rust.plaoc.webView.network.dWebView_host
import org.bfchain.rust.plaoc.webView.network.shakeUrl
import org.bfchain.rust.plaoc.webView.urlscheme.CustomUrlScheme
import org.bfchain.rust.plaoc.webView.urlscheme.requestHandlerFromAssets
import org.bfchain.rust.plaoc.webkit.AdAndroidWebView
import org.bfchain.rust.plaoc.webkit.rememberAdWebViewState
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.io.path.Path

private const val TAG = "DWebViewActivity"
var dWebView: AdAndroidWebView? = null

class DWebViewActivity : AppCompatActivity() {

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
        WebView.setWebContentsDebuggingEnabled(true)// 开启调试
        // 设置装饰视图是否应适合WindowInsetsCompat(Describes a set of insets for window content.)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val activity = this
        setContent {
            RustApplicationTheme {
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

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)
@Composable
private fun NavFun(activity: ComponentActivity) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController = navController, startDestination = "https://{url}") {
            composable(
                "https://{url}",
                arguments = listOf(
                    navArgument("url") {
                        type = NavType.StringType
                    }
                ),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://{url}"
                })
            ) { entry ->
              Log.d(TAG, "NavFun entry : ${entry.arguments?.getString("url")}")
                // 请求文件路径
                var urlStr = entry.arguments?.getString("url")
                    .let { it -> URLDecoder.decode(it, "UTF-8") }


                val host = Path(urlStr).getName(1).toString()
                val assetBasePath = "./"
//                Log.d(TAG, "NavFun host : $host")
//                Log.d(TAG, "NavFun urlStr : $urlStr")
                // 设置规则
                val customUrlScheme = CustomUrlScheme(
                    "https", host,
                    requestHandlerFromAssets(LocalContext.current.assets, assetBasePath)
                )
                DWebView(
                    state = rememberAdWebViewState(urlStr),
                    navController = navController,
                    activity = activity,
                    modifier = Modifier.background(Color.Unspecified),
                    customUrlScheme = customUrlScheme,
                ) { webView ->
                    dWebView = webView
                }
            }
        }
    }
}
/** 打开DWebview*/
fun openDWebWindow(activity: ComponentActivity, url: String) {
    var intent = Intent(activity.applicationContext, DWebViewActivity::class.java).also {
        it.data = Uri.parse("https://"+URLEncoder.encode(url, "UTF-8"))
    }
    activity.startActivity(intent)
}

/** 传递参数给前端*/
fun sendToJavaScript(message: String) {
    Log.i("xxx", "sendToJavaScript->:$message")
    dWebView?.post(Runnable {
        dWebView?.evaluateJavascript(message) {
//            Log.d(TAG, "sendToJavaScript: $response")
        }
    })

}



