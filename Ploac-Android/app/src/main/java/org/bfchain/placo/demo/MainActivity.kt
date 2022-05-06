package org.bfchain.placo.demo

//import com.google.accompanist.web.Webview
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.web.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.bfchain.placo.demo.ui.theme.PlacoDemoTheme
import java.net.URLDecoder
import java.net.URLEncoder


private val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this;
        android.webkit.WebView.setWebContentsDebuggingEnabled(true)
        Log.i(TAG, "onCreate")

        setContent {
            PlacoDemoTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavFun(activity = activity)
                }
            }
        }
    }
}

val webStateMap: MutableMap<String, WebViewState> = mutableMapOf();
val getWebState = { url: String ->
    var state = webStateMap[url] ?: createWebState(url);
    webStateMap[url] = state;
    state
}
val createWebState = { url: String ->
    WebViewState(
        WebContent.Url(
            url = url,
        )
    );
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun NavFun(activity: ComponentActivity) {

//                val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)


    ModalBottomSheetLayout(bottomSheetNavigator) {

        AnimatedNavHost(navController = navController, startDestination = "profile") {
            composable("home") { Greeting("Home!!") }
            composable("profile") { Profile(navController, activity) }
            composable("friendslist") { FriendsList() }
            composable("web") {
                DwebIntel(navController = navController)

//                Text("gg")

//                DwebView(
//                    getWebState("file:///android_asset/example.html"),
//                    navController,
//                    activity
//                )
            }
            composable("demo-web/{url}", arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                }
            )) { entry ->
                val url = entry.arguments?.getString("url")
                    .let { it -> URLDecoder.decode(it, "UTF-8") }
                    ?: "file:///android_asset/demo.html"

                DWebViewList(
                    getWebState(url),
                    navController,
                    activity
                )
            }
            bottomSheet(route = "sheet") {
                Text("This is a cool bottom sheet!")
            }

        }
    }

}


@Composable
private fun DwebIntel(navController: NavController) {
    LaunchedEffect(Unit) {
        println("LaunchedEffect: entered main")
        var i = 0
        // Just an example of coroutines usage
        // don't use this way to track screen disappearance
        // DisposableEffect is better for this
        try {
            while (true) {
                delay(1000)
                println("LaunchedEffect: ${i++} sec passed")
            }
        } catch (cancel: CancellationException) {
            println("LaunchedEffect: job cancelled")
        }
    }
    DisposableEffect(Unit) {
        println("DisposableEffect: entered main")
        onDispose {
            println("DisposableEffect: exited main")
        }
    }
    Text("qaq")
}

val urlList = mutableListOf<WebViewState>()

@SuppressLint("JavascriptInterface")
@Composable
private fun DWebViewList(
    webviewState: WebViewState,
    navController: NavController,
    activity: ComponentActivity
) {
    var urlList by remember {
        mutableStateOf(setOf<WebViewState>())
    }

//    val exampleState = rememberWebViewState(url)

//    LaunchedEffect(Unit) {
    urlList += webviewState;
//    }
    DisposableEffect(Unit) {
        onDispose {
            urlList -= webviewState;
        }
    }
    Column {
        Text("qaq")
        urlList.forEach { state ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(2.dp, Color.Red)
            ) {
                DWebView(webviewState = state, navController = navController, activity = activity)
            }
        }
        Text("mml")
    }
}

@Composable
private fun Profile(navController: NavController, activity: ComponentActivity) {
    var value by rememberSaveable { mutableStateOf("Hello\nWorld\nInvisible") }

    val context = LocalContext.current
    Column() {

        Button(onClick = {
            navController.navigate("friendslist") {
                popUpTo("home")
            }
        }) {
            Text(text = "Navigate next")
        }
        Button(onClick = {

//            navController.navigate(
//                "demo-web/${
//                    URLEncoder.encode(
//                        "file:///android_asset/example.html",
//                        "UTF-8"
//                    )
//                }"
//            ) {
//                popUpTo("friendslist")
//            }
//                            openDWebWindow(activity, url);


            openDWebWindow(activity = activity, url = "file:///android_asset/example.html")

            Log.i(TAG, "startActivity!!!")
        }) {
            Text(text = "Go Web")
        }
        Button(onClick = {
            navController.navigate("sheet" + "?arg=From Home Screen")
        }) {
            Text(text = "Show Sheet")
        }

        TextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Enter text") },
            maxLines = 2,
            textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(20.dp)
        )
    }
}


@Composable
private fun FriendsList() {
    Column() {
        Text(text = "Friend KangKang!")
        Text(text = "FriendsList Dixie!")
        Text(text = "FriendsList Jane!")
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlacoDemoTheme {
        Greeting("Android")
    }
}