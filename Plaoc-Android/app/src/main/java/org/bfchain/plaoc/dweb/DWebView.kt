package org.bfchain.plaoc.dweb

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Message
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.AddCircle
import androidx.compose.material.primarySurface
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.bfchain.plaoc.dweb.js.navigator.NavigatorFFI
import org.bfchain.plaoc.dweb.js.systemUi.*
import org.bfchain.plaoc.dweb.js.util.JsUtil
import org.bfchain.plaoc.webkit.*
import kotlin.math.min


private const val TAG = "DWebView"

inline fun <T : Any> multiLet(vararg elements: T, closure: (List<T>) -> Unit) {
    closure(elements.asList())
}

@SuppressLint("RememberReturnType")
@Composable
fun <T : Any, R> rememberLet(
    vararg elements: T,
    closure: (Array<out T>) -> R
): R {
    return remember(*elements) {
        Log.i(TAG, "rememberLet-CHAGED!!!!")
        closure(elements)
    }
}

@ExperimentalLayoutApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("JavascriptInterface")
@Composable
fun DWebView(
    state: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
    onCreated: (AdAndroidWebView) -> Unit = {},
) {
    var jsUtil by remember {
        mutableStateOf<JsUtil?>(null)
    }
    val hook = remember {
        AdWebViewHook()
    }

    val systemUiController = rememberSystemUiController()
    val isOverlayStatusBar = remember { mutableStateOf(false) }
    val isOverlayNavigationBar = remember { mutableStateOf(false) }


    val isOverlayVirtualKeyboard = remember {
        mutableStateOf(false)
    }
    jsUtil?.apply {
        InputMethodEditorFFI.InjectVirtualKeyboardVars(
            this,
            LocalDensity.current, LocalLayoutDirection.current,
            isOverlayVirtualKeyboard.value, WindowInsets.ime,
            isOverlayNavigationBar.value, WindowInsets.navigationBars,
        )
    }


    var overlayOffset = IntOffset(0, 0);
    val overlayPadding = WindowInsets.statusBars.let {
        if (!isOverlayVirtualKeyboard.value && WindowInsets.isImeVisible) {
//            it.add(WindowInsets.ime) // ime本身就包含了navigationBars的高度
            overlayOffset =
                IntOffset(
                    0, min(
                        0, -WindowInsets.ime.getBottom(LocalDensity.current)
                                + WindowInsets.navigationBars.getBottom(LocalDensity.current)
                    )
                )
        }
        it.add(WindowInsets.navigationBars)
    }.asPaddingValues()

    Log.i(TAG, "overlayPadding:$overlayPadding")

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
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = topBarTitle.value ?: webTitleContent,
                    modifier = Modifier
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
            colors = @Stable object : TopAppBarColors {
                @Composable
                override fun actionIconContentColor(scrollFraction: Float): State<Color> {
                    return topBarForegroundColor
                }

                @Composable
                override fun containerColor(scrollFraction: Float): State<Color> {
                    return topBarBackgroundColor
                }

                @Composable
                override fun navigationIconContentColor(scrollFraction: Float): State<Color> {
                    return topBarForegroundColor
                }

                @Composable
                override fun titleContentColor(scrollFraction: Float): State<Color> {
                    return topBarForegroundColor
                }
            },
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
        mutableStateOf<Boolean?>(null)
    }
    val bottomBarOverlay = remember {
        mutableStateOf(false)
    }
    val bottomBarHeight = remember {
        mutableStateOf<Float?>(null)
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

    val bottomBarFFI = remember {
        BottomBarFFI(
            bottomBarEnabled,
            bottomBarOverlay,
            bottomBarHeight,
            bottomBarActions,
            bottomBarBackgroundColor,
            bottomBarForegroundColor,
        )
    }


    @Composable
    fun MyBottomAppBar() {
        NavigationBar(
            modifier = Modifier
                .let {
                    bottomBarHeight.value?.let { height ->
                        if (height >= 0) {
                            it.height(height.dp)
                        } else {
                            it
                        }
                    } ?: it
                }
                .onGloballyPositioned { coordinates ->
                    bottomBarHeight.value = coordinates.size.height / localDensity.density
                },
            containerColor = bottomBarBackgroundColor.value,
            contentColor = bottomBarForegroundColor.value,
            tonalElevation = 0.dp,
        ) {
            if (bottomBarActions.size > 0) {
                var selectedItem by remember(bottomBarActions) {
                    mutableStateOf(bottomBarActions.indexOfFirst { it.selected }
                        .let { if (it == -1) 0 else it })
                }
                bottomBarActions.forEachIndexed { index, action ->
                    NavigationBarItem(
                        icon = {
                            DWebIcon(action.icon)
                        },
                        label = if (action.label.isNotEmpty()) {
                            { Text(action.label) }
                        } else {
                            null
                        },
                        enabled = !action.disabled,
                        onClick = {
                            if (action.selectable) {
                                selectedItem = index
                            }
                            jsUtil?.evalQueue { action.onClickCode }
                        },
                        selected = selectedItem == index,
                        colors = action.colors?.toNavigationBarItemColors()
                            ?: NavigationBarItemDefaults.colors(),
                    )
                }
            }
        }
    }



    Scaffold(
        modifier = modifier
            .padding(overlayPadding)
            .offset { overlayOffset },
        topBar = { if (!topBarOverlay.value and topBarEnabled.value) MyTopAppBar() },
        bottomBar = { if (!bottomBarOverlay.value and bottomBarFFI.isEnabled) MyBottomAppBar() },
        content = { innerPadding ->
            DWebBackground(innerPadding, hook, activity)

            Log.i(TAG, "innerPadding:${innerPadding}");
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

                    webView.addJavascriptInterface(bottomBarFFI, "bottom_bar")

                    val inputMethodEditorFFI = InputMethodEditorFFI(isOverlayVirtualKeyboard)
                    webView.addJavascriptInterface(inputMethodEditorFFI, "input_method_eidtor")

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
                    var bottom = innerPadding.calculateBottomPadding()
                    val layoutDirection = LocalLayoutDirection.current;
                    var start = innerPadding.calculateStartPadding(layoutDirection)
                    var end = innerPadding.calculateEndPadding(layoutDirection)
                    if (topBarOverlay.value or !topBarEnabled.value) {
                        top = 0.dp;
                    }
                    if (bottomBarOverlay.value or !bottomBarFFI.isEnabled) {
                        bottom = 0.dp
                    }
                    if ((top.value == 0F) and (bottom.value == 0F)) {
                        start = 0.dp; end = 0.dp;
                    }
                    Log.i(TAG, "webview-padding $start, $top, $end, $bottom")
                    m.padding(start, top, end, bottom)
                },
            )

            if (topBarOverlay.value and topBarEnabled.value) MyTopAppBar()
            if (bottomBarOverlay.value and bottomBarFFI.isEnabled) {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxSize()
                ) { MyBottomAppBar() }
            }

        },
        containerColor = Companion.Transparent,
//        contentColor = Companion.Transparent
    )

}

private operator fun <T> Array<T>.component6(): Any {
    return this.elementAt(5) as Any
}

@Composable
private fun DWebBackground(
    innerPadding: PaddingValues,
    hook: AdWebViewHook,
    activity: ComponentActivity
) {
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
}
