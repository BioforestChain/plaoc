package org.bfchain.plaoc.dweb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import org.bfchain.plaoc.dweb.DWebView
import org.bfchain.plaoc.ui.theme.PlaocTheme
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ALL.add(this)

        android.webkit.WebView.setWebContentsDebuggingEnabled(true)
        Log.i(TAG, "onCreate")
        Log.i(TAG, "actionBar$actionBar")


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
                Column {
                    Text(text = "This is Native Text in Background~")
                    Text(text = "这是Android原生的文本")
                    Text(text = "If you saw those text, means you changed webView background.")
                    Text(text = "如果你看到这些文本，说明你改变了webView的背景透明度")
                    Text(text = "So you can put native view in background.")
                    Text(text = "所以，你可以防止一些原生的视图在背景上")
                    Button(onClick = {
                        Toast.makeText(
                            activity.applicationContext,
                            "Showing toast....",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Text(text = "甚至时可以交互的原生按钮")
                    }
                }
                DWebView(rememberAdWebViewState(entry.arguments?.getString("url")
                    .let { it -> URLDecoder.decode(it, "UTF-8") }
                    ?: "file:///android_asset/demo.html"),
                    navController = navController,
                    activity = activity,
                    modifier = Modifier.clickable(true) {
                        Toast.makeText(
                            activity.applicationContext,
                            "Click WebView",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(TAG, "Click WebView")
                    }
                )

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