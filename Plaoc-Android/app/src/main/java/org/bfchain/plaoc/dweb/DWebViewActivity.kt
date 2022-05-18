package org.bfchain.plaoc.dweb

import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.ActionMode
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.bfchain.plaoc.R
import org.bfchain.plaoc.dweb.js.navigator.NavigatorFFI
import org.bfchain.plaoc.dweb.js.systemUi.SystemUiFFI
import org.bfchain.plaoc.dweb.js.util.JsUtil
import org.bfchain.plaoc.ui.theme.PlaocTheme
import org.bfchain.plaoc.webkit.AdWebChromeClient
import org.bfchain.plaoc.webkit.AdWebView
import org.bfchain.plaoc.webkit.AdWebViewHook
import org.bfchain.plaoc.webkit.rememberAdWebViewState
import java.net.URLDecoder
import java.net.URLEncoder

private const val TAG = "DWebViewActivity"

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

    override fun onActionModeStarted(mode: ActionMode?) {
        mode?.menu?.let { menu ->
//            it.clear()
            menu.add("qaq").apply {
                this.icon = R.mipmap.ic_launcher.toDrawable()
                this.setOnMenuItemClickListener {
                    Log.i(TAG, "qaq")
                    mode.finish()
                    true;
                }
            }
            menu.addSubMenu("zzz").apply {
                this.add("xxx").apply {
                    this.setOnMenuItemClickListener {
                        Log.i(TAG, "xxx")
                        mode.finish()
                        true;
                    }
                }
            }
            menu.addSubMenu("www").apply {
                this.add("yyy").apply {
                    this.setOnMenuItemClickListener {
                        Log.i(TAG, "yyy")
                        mode.finish()
                        true;
                    }
                }
            }
        }
//        mode.
        super.onActionModeStarted(mode)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ALL.add(this)

        android.webkit.WebView.setWebContentsDebuggingEnabled(true)
        Log.i(TAG, "onCreate")
        Log.i(TAG, "actionBar$actionBar")
        WindowCompat.setDecorFitsSystemWindows(window, false)

//                val navController = rememberNavController()
        val activity = this;
        setContent {
            PlaocTheme {
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

    var adWebViewHook by remember {
        mutableStateOf<AdWebViewHook?>(null)
    }

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController = navController, startDestination = "dweb/{url}") {
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
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "~~~", fontSize = 30.sp)
                    Text(text = "这是Android原生的文本")
                    Text(text = "他们渲染在WebView的背景上")
                    Text(text = "如果你看到这些内容，就说明html的背景是有透明度的")
                    Text(text = "你可以通过 SystemUI 的 Disable Touch Event 来禁用于 WebView 的交互，从而获得与这一层 Native 交互的能力")
                    Button(onClick = {
                        adWebViewHook?.onTouchEvent = null;
                        Toast.makeText(
                            activity.applicationContext,
                            "控制权已经回到Web中了",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Text(text = "归还Web的交互权")
                    }
                    Text(text = "~~~", fontSize = 30.sp)
                    Text(text = "也就是说，你可以将一些原生的组件防止在这里，仅仅提供渲染，而Web层提供控制与逻辑执行，比如说游戏的渲染")
                    Text(text = "~~~", fontSize = 30.sp)
                    Text(text = "又或者，我们也可以制定部分的WebView区域是可控制的，其余的穿透到native层")
                }


                DWebView(
                    state = rememberAdWebViewState(entry.arguments?.getString("url")
                        .let { it -> URLDecoder.decode(it, "UTF-8") }
                        ?: "file:///android_asset/demo.html"),
                    navController = navController,
                    activity = activity,
                ) { webView ->
                    adWebViewHook = webView.adWebViewHook
                }
            }
        }
    }
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