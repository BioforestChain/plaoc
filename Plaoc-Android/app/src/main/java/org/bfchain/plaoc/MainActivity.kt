package org.bfchain.plaoc

//import com.google.accompanist.web.Webview
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
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
import org.bfchain.plaoc.R
import org.bfchain.plaoc.bfs.Boot
import org.bfchain.plaoc.plugin.scanner.QrCodeAnalyzer
import org.bfchain.plaoc.ui.theme.PlaocTheme
import java.lang.Exception
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
private fun MyScaffold(
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

@Composable
fun QrCodeScanner(navController: NavController) {
    var code by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(android.Manifest.permission.CAMERA)
    }


    MyScaffold(navController, "QrCodeScanner") { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            if (hasCamPermission) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(text = "正在使用您都相机权限", modifier = Modifier.fillMaxWidth())
                    AndroidView(
                        factory = { context ->

                            val previewView = PreviewView(context).also { it ->
                                it.scaleType = PreviewView.ScaleType.FIT_CENTER
                                it.previewStreamState.observe(lifecycleOwner) { state ->
                                    Log.i(
                                        TAG,
                                        "previewView size(${state.name}): ${it.width}x${it.height}"
                                    )
                                    if (state.name == "STREAMING") {

                                    }
                                };
                            }

                            val preview = androidx.camera.core.Preview.Builder().build()
                            val selector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build()
                            preview.setSurfaceProvider(previewView.surfaceProvider)

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            Log.i(
                                TAG,
                                "previewView size: ${previewView.width}x${previewView.height}=${imageAnalysis.resolutionInfo}"
                            )


                            imageAnalysis.setAnalyzer(
                                ContextCompat.getMainExecutor(context),
                                QrCodeAnalyzer { result ->
                                    code = result
                                }
                            )

                            try {
                                cameraProviderFuture.get().bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )

                            previewView
                        },
                    )
                    Text(
                        text = code,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }

            } else {
                Text(text = "未取得相机权限")
                Button(onClick = {
                    launcher.launch(Manifest.permission.CAMERA)
                }) {
                    Text(text = "授权相机权限")
                }
            }
        }
    }
}



