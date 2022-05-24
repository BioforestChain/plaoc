package org.bfchain.plaoc.dweb

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Message
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.bfchain.plaoc.R
import org.bfchain.plaoc.dweb.js.navigator.NavigatorFFI
import org.bfchain.plaoc.dweb.js.systemUi.*
import org.bfchain.plaoc.dweb.js.util.JsUtil
import org.bfchain.plaoc.webkit.*


private const val TAG = "DWebView"


@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("JavascriptInterface", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DWebView(
    state: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
    onCreated: (AdAndroidWebView) -> Unit = {},
) {

    val svgLoader by remember(activity) {
        lazy {
            ImageLoader.Builder(activity.applicationContext)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
        }
    }
    val systemUiController = rememberSystemUiController()
    val isOverlayStatusBar = remember { mutableStateOf(false) }
    val isOverlayNavigationBar = remember { mutableStateOf(false) }
    val insetsCompat = remember(activity.window.decorView.rootWindowInsets) {
        WindowInsetsCompat.toWindowInsetsCompat(
            activity.window.decorView.rootWindowInsets
        )
    }
    var jsUtil by remember {
        mutableStateOf<JsUtil?>(null)
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

    var webTitleContent by remember {
        mutableStateOf("")
    }

    val topBarEnabled = remember {
        mutableStateOf(true)
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
    val topBarActions = remember {
        mutableStateListOf<TopBarAction>()
    }
    val topBarBackgroundColor = MaterialTheme.colors.primarySurface.let { defaultPrimarySurface ->
        remember {
            mutableStateOf(defaultPrimarySurface)
        }
    }
    // https://developer.android.com/jetpack/compose/themes/material#emphasis
    // Material Design 文本易读性建议一文建议通过不同的不透明度来表示不同的重要程度。
    // TopAppBar 组件内部已经强制写死了 CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high)
    val topBarForegroundColor = MaterialTheme.colors.contentColorFor(topBarBackgroundColor.value)
        .let { defaultContentColor ->
            remember {
                mutableStateOf(defaultContentColor)
            }
        }

    val localDensity = LocalDensity.current

    @Composable
    fun MyTopAppBar() {
        TopAppBar(
            title = {
                Text(
                    text = topBarTitle.value ?: webTitleContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
//                        .pointerInteropFilter { event ->
//                            Log.i(TAG, "filter Title event $event")
//                            false
//                        }
//                        .clickable {
//                            Log.i(TAG, "Clicked Title")
//                        }
                )
            },
            actions = {
                if (topBarActions.size > 0) {
                    for (action in topBarActions) {
                        IconButton(
                            onClick = {
                                jsUtil?.evalQueue { action.onClickCode }
                            },
                            enabled = !action.disabled
                        ) {
                            DWebIcon(action.icon)
                        }
                    }
                } else {
                    /// 占位，只是为了让 title 居中
                    IconButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.clickable(false) {}) { }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        activity.onBackPressed()
                    }, modifier = Modifier
//                        .pointerInteropFilter { event ->
//                            Log.i(TAG, "filter NavigationIcon event $event")
//                            true
//                        }
//                        .clickable {
//                            Log.i(TAG, "Clicked NavigationIcon")
//                        }
                ) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
            backgroundColor = topBarBackgroundColor.value,
            contentColor = topBarForegroundColor.value,
            elevation = 0.dp,
            modifier = Modifier
//                .graphicsLayer(
//                    renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) createBlurEffect(
//                        25f,
//                        25f,
//                        Shader.TileMode.MIRROR
//                    ).asComposeRenderEffect() else null
//                )
                .onGloballyPositioned { coordinates ->
                    topBarHeight.value = coordinates.size.height / localDensity.density
                }
//                .pointerInteropFilter { event ->
//                    Log.i(TAG, "filter TopAppBar event $event")
//
//                    // false 会穿透，在穿透后，返回按钮也能点击了
//                    // true 不会穿透，但是返回按钮也无法点击了
//                    false
//                }.clickable {
//                    Log.i(TAG, "Clicked TopAppBar")
//                }
        )
    }


    val bottomBarEnabled = remember {
        mutableStateOf(true)
    }
    val bottomBarOverlay = remember {
        mutableStateOf(false)
    }
    val bottomBarHeight = remember {
        mutableStateOf(0F)
    }
    val bottomBarActions = remember {
        mutableStateListOf<BottomBarAction>()
    }

    val bottomBarBackgroundColor =
        MaterialTheme.colors.primarySurface.let { defaultPrimarySurface ->
            remember {
                mutableStateOf(defaultPrimarySurface)
            }
        }
    val bottomBarForegroundColor =
        MaterialTheme.colors.contentColorFor(bottomBarBackgroundColor.value)
            .let { defaultContentColor ->
                remember {
                    mutableStateOf(defaultContentColor)
                }
            }


    @Composable
    fun MyBottomAppBar() {
        BottomAppBar(
            elevation = 0.dp,
            modifier = Modifier.onGloballyPositioned { coordinates ->
                bottomBarHeight.value = coordinates.size.height / localDensity.density
            },
            backgroundColor = bottomBarBackgroundColor.value,
            contentColor = bottomBarForegroundColor.value,
        ) {

        }
    }



    Scaffold(
        modifier = modifier.padding(overlayPadding),
        bottomBar = { if (!bottomBarOverlay.value) MyBottomAppBar() },
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
                    jsUtil = JsUtil(activity, evaluateJavascript = { code, callback ->
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
                        jsUtil!!,
                        isOverlayStatusBar,
                        isOverlayNavigationBar
                    )
                    webView.addJavascriptInterface(systemUiFFI, "system_ui")

                    val topBarFFI = TopBarFFI(
                        { activity.onBackPressed() },
                        topBarEnabled,
                        topBarOverlay,
                        topBarTitle,
                        topBarHeight,
                        topBarActions,
                        topBarBackgroundColor,
                        topBarForegroundColor,
                    )
                    webView.addJavascriptInterface(topBarFFI, "top_bar")

                    val bottomBarFFI = BottomBarFFI(
                        bottomBarEnabled,
                        bottomBarOverlay,
                        bottomBarHeight,
                        bottomBarActions,
                        bottomBarBackgroundColor,
                        bottomBarForegroundColor,
                    )
                    webView.addJavascriptInterface(topBarFFI, "bottom_bar")

                    onCreated(webView)
                },
                chromeClient = remember {
                    class MyWebChromeClient : AdWebChromeClient() {
                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            super.onReceivedTitle(view, title);
                            webTitleContent = title ?: ""
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
            if (bottomBarOverlay.value) MyBottomAppBar()

        },
        backgroundColor = Companion.Transparent,
//        contentColor = Companion.Transparent
    )

}


