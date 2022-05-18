package org.bfchain.plaoc

//import com.google.accompanist.web.Webview
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.bfchain.plaoc.bfs.Boot
import org.bfchain.plaoc.dweb.DWebView
import org.bfchain.plaoc.dweb.openDWebWindow
import org.bfchain.plaoc.plugin.scanner.QrCodeScanner
import org.bfchain.plaoc.ui.theme.PlaocTheme
import org.bfchain.plaoc.webkit.AdWebContent
import org.bfchain.plaoc.webkit.AdWebViewState
import java.net.URLDecoder


private val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this;
        android.webkit.WebView.setWebContentsDebuggingEnabled(true)
        Log.i(TAG, "onCreate")

        setContent {
            PlaocTheme {

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

val webStateMap: MutableMap<String, AdWebViewState> = mutableMapOf();
val getWebState = { url: String ->
    var state = webStateMap[url] ?: createWebState(url);
    webStateMap[url] = state;
    state
}
val createWebState = { url: String ->
    AdWebViewState(
        AdWebContent.Url(
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
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)

    val durationMillis = 400 * 3;

    ModalBottomSheetLayout(
        bottomSheetNavigator,
//                modifier = Modifier.padding(innerPadding)
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = "profile",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis)
                )//.plus(fadeIn(animationSpec = tween(durationMillis)))
//                expandIn(animationSpec = tween(durationMillis))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis),
                    targetOffset = { it -> (it * 0.25).toInt() },
                )
//                shrinkOut(animationSpec = tween(durationMillis))
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis),
                    targetOffset = { it -> (it * 0.25).toInt() },
                )//.plus(fadeOut(animationSpec = tween(durationMillis)))
            },

            ) {
            composable("home") { Greeting("Home!!") }
            composable("profile") { Profile(navController, activity) }
            composable("friendslist") { FriendsList(navController) }
            composable("qrcode") { QrCodeScanner(navController) }
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

@SuppressLint("JavascriptInterface")
@Composable
private fun DWebViewList(
    webviewState: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity
) {
    var urlList by remember {
        mutableStateOf(setOf<AdWebViewState>())
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
                DWebView(webViewState = state, navController = navController, activity = activity)
            }
        }
        Text("mml")
    }
}

@Composable
fun MyScaffold(
    navController: NavController,
    title: String,
    bodyContentBuilder: @Composable (Modifier) -> Unit
) {
    val canGoBack by remember {
        mutableStateOf(navController.backQueue.size > 2)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = if (canGoBack) {
                    {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "backIcon")
                        }
                    }
                } else {
                    null
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                elevation = 10.dp
            )
        },
        content = { innerPadding ->
            bodyContentBuilder(Modifier.padding(innerPadding))
        }
    )
}

@Composable
private fun Profile(navController: NavController, activity: ComponentActivity) {
    var value by rememberSaveable { mutableStateOf("Hello\nWorld\nInvisible") }

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = { result ->
            Log.i(TAG, "PickContact Result:$result")
        });

    val context = LocalContext.current
    MyScaffold(navController, "QrCodeScanner") { modifier ->
        Column(modifier = modifier) {
            Button(onClick = { navController.navigate("qrcode") }) {
                Text(text = "qrcode scanner")
            }
            Button(onClick = {
                navController.navigate("friendslist") {
                    popUpTo("home")
                }
            }) {
                Text(text = "Navigate next")
            }
            Button(onClick = {
//                val intent = Intent(
//                    Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                startActivityForResult(activity, intent, IntentUtil.instance.openImageGalleryCode);

            }) {
                Text(text = "选择图片")
            }
            Button(onClick = {
                val bfs = Boot(activity);
            }) {
                Text(text = "Start BFS")
            }
            Button(onClick = {

                openDWebWindow(activity = activity, url = "file:///android_asset/example.html")

                Log.i(TAG, "startActivity!!!")
            }) {
                Text(text = "Go Web Example")
            }
            Button(onClick = {

                openDWebWindow(activity = activity, url = "file:///android_asset/statusbar.html")

                Log.i(TAG, "startActivity!!!")
            }) {
                Text(text = "Go Web StatusBar")
            }
            Button(onClick = {
                navController.navigate("sheet" + "?arg=From Home Screen")
            }) {
                Text(text = "Show Sheet")
            }
            Button(onClick = {
                pickContactLauncher.launch()
            }) {
                Text("获取联系人")
            }

//            Button(onClick = {
//                activityLauncher.launch()
//            }) {
//                Text("打开文档")
//            }

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
}


@Composable
private fun FriendsList(navController: NavController) {
    MyScaffold(navController, title = "FriendsList") { modifier ->
        Column(modifier) {
            Text(text = "Friend KangKang!${R.xml.root_preferences}")
            Text(text = "FriendsList Dixie!")
            Text(text = "FriendsList Jane!")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlaocTheme {
        Greeting("Android")
    }
}



