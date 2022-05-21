package org.bfchain.plaoc.dweb

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Message
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.bfchain.plaoc.dweb.js.navigator.NavigatorFFI
import org.bfchain.plaoc.dweb.js.systemUi.SystemUiFFI
import org.bfchain.plaoc.dweb.js.systemUi.TopBarFFI
import org.bfchain.plaoc.dweb.js.util.JsUtil
import org.bfchain.plaoc.webkit.*


private const val TAG = "DWebView"

@SuppressLint("JavascriptInterface", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DWebView(
    state: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
    onCreated: (AdAndroidWebView) -> Unit = {},
) {
    val systemUiController = rememberSystemUiController()
    val isOverlayStatusBar = remember { mutableStateOf(false) }
    val isOverlayNavigationBar = remember { mutableStateOf(false) }
    val insetsCompat = remember(activity.window.decorView.rootWindowInsets) {
        WindowInsetsCompat.toWindowInsetsCompat(
            activity.window.decorView.rootWindowInsets
        )
    }

    val hook = remember {
        AdWebViewHook()
    }

    var typeMask = 0
    if (!isOverlayStatusBar.value) {
        typeMask = typeMask or WindowInsetsCompat.Type.statusBars()
    }
    if (!isOverlayNavigationBar.value) {
        typeMask = typeMask or WindowInsetsCompat.Type.navigationBars()
    }
    val overlayInsets = insetsCompat.getInsets(typeMask)
    val overlayPadding = with(LocalDensity.current) {
        PaddingValues(
            top = overlayInsets.top.toDp(),
            bottom = overlayInsets.bottom.toDp(),
            // @TODO 这里应该判定布局的方向
            start = overlayInsets.left.toDp(),
            end = overlayInsets.right.toDp(),
        )
    }
    val overlayTopPadding = with(LocalDensity.current) {
        PaddingValues(
            top = overlayInsets.top.toDp(),
            // @TODO 这里应该判定布局的方向
            start = overlayInsets.left.toDp(),
            end = overlayInsets.right.toDp(),
        )
    }

    val overlayBottomPadding = with(LocalDensity.current) {
        PaddingValues(
            bottom = overlayInsets.bottom.toDp(),
            // @TODO 这里应该判定布局的方向
            start = overlayInsets.left.toDp(),
            end = overlayInsets.right.toDp(),
        )
    }

    var titleContent by remember {
        mutableStateOf("")
    }

    val topBarOverlay = remember {
        mutableStateOf(false)
    }
    val topBarTitle = remember {
        mutableStateOf<String?>(null)
    }
    val topBarHeight = remember {
        mutableStateOf(0F)
    }
    val localDensity = LocalDensity.current

    @Composable
    fun MyTopAppBar() {
        TopAppBar(
            title = {
                Text(
                    text = topBarTitle.value ?: titleContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            actions = {
                IconButton(onClick = {
//                    val classLoader = URLClassLoader(
//                        arrayOf<URL>(URL("file:///c:/projects/myProject/bin/")),
//                        javaClass.classLoader
//                    )
//                    val z =androidx.compose.material.icons.Icons
//                    val reflections = Reflections("my.project.prefix")
//                    Log.i(TAG, setOf(Icons.Default))


//                    Class.forName("androidx.compose.material.icons.Icons").classes.forEach { c ->
//                        try {
//                            Log.i(
//                                TAG,
//                                "Icons.Filled.${c.name}(${
//                                    c.declaredMethods.first()
//                                        .invoke(null, Icons.Filled) as ImageVector
//                                })"
//                            )
//                        } catch (_: Throwable) {
//                        }
//                    }
                }) {
                    IconByName("Filled.Menu")
//                    Icon(Icons.Filled.Menu, "menuIcon")
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
            backgroundColor = Companion.Transparent,
            contentColor = androidx.compose.ui.graphics.Color.White,
            elevation = 0.dp,
            modifier = Modifier.onGloballyPositioned { coordinates ->

                topBarHeight.value = coordinates.size.height / localDensity.density
            }
        )
    }


    val bottomBarOverlay = remember {
        mutableStateOf(false)
    }

    @Composable
    fun MyBottonBar() {
        BottomAppBar(elevation = 0.dp) {

        }
    }

    Scaffold(
        modifier = modifier.padding(overlayPadding),
        bottomBar = {},
        topBar = { if (!topBarOverlay.value) MyTopAppBar() },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(10.dp)
            ) {
                Text(text = "~~~", fontSize = 30.sp)
                Text(text = "这是Android原生的文本")
                Text(text = "他们渲染在WebView的背景上")
                Text(text = "如果你看到这些内容，就说明html的背景是有透明度的")
                Text(text = "你可以通过 SystemUI 的 Disable Touch Event 来禁用于 WebView 的交互，从而获得与这一层 Native 交互的能力")
                Button(onClick = {
                    hook?.onTouchEvent = null;
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

            AdWebView(
                state = state,
                onCreated = { webView ->
                    // 将webView的背景默认设置为透明。不通过systemUi的api提供这个功能，一些手机上动态地修改webView背景颜色，在黑夜模式下，会有问题
                    webView.setBackgroundColor(Companion.Transparent.toArgb());
                    webView.adWebViewHook = hook;

                    // 通用的工具类
                    val jsUtil = JsUtil(activity, evaluateJavascript = { code, callback ->
                        webView.evaluateJavascript(
                            code,
                            callback
                        )
                    });

                    val navigatorFFI = NavigatorFFI(webView, activity, navController)
                    webView.addJavascriptInterface(navigatorFFI, "my_nav");

                    val systemUiFFI = SystemUiFFI(
                        activity,
                        webView,
                        hook,
                        systemUiController,
                        jsUtil,
                        isOverlayStatusBar,
                        isOverlayNavigationBar
                    )
                    webView.addJavascriptInterface(systemUiFFI, "system_ui")

                    val topBarFFI = TopBarFFI(
                        topBarOverlay,
                        topBarTitle,
                        topBarHeight,
                    )
                    webView.addJavascriptInterface(topBarFFI, "top_bar")

                    onCreated(webView)
                },
                chromeClient = remember {
                    class MyWebChromeClient : AdWebChromeClient() {
                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            super.onReceivedTitle(view, title);
                            titleContent = title ?: ""
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
                modifier = Modifier.let { m ->
                    var top = innerPadding.calculateTopPadding()
                    var bottom = innerPadding.calculateTopPadding()
                    val layoutDirection = LocalLayoutDirection.current;
                    var start = innerPadding.calculateStartPadding(layoutDirection)
                    var end = innerPadding.calculateEndPadding(layoutDirection)
                    if (topBarOverlay.value) {
                        top = 0.dp;
                    }
                    if (bottomBarOverlay.value) {
                        bottom = 0.dp
                    }
                    if (topBarOverlay.value and bottomBarOverlay.value) {
                        start = 0.dp; end = 0.dp;
                    }
                    Log.i(TAG, "webview-padding $start, $top, $end, $bottom")
                    m.padding(start, top, end, bottom)
                },
            )

            if (topBarOverlay.value) MyTopAppBar()

        },
        backgroundColor = Companion.Transparent,
//        contentColor = Companion.Transparent
    )

}


