package org.bfchain.plaoc.dweb

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Build
import android.os.Message
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bfchain.plaoc.dweb.bottombar.BottomBarFFI
import org.bfchain.plaoc.dweb.bottombar.BottomBarState
import org.bfchain.plaoc.dweb.bottombar.DWebBottomBar
import org.bfchain.plaoc.dweb.dialog.*
import org.bfchain.plaoc.dweb.js.util.JsUtil
import org.bfchain.plaoc.dweb.navigator.NavigatorFFI
import org.bfchain.plaoc.dweb.systemui.SystemUIState
import org.bfchain.plaoc.dweb.systemui.SystemUiFFI
import org.bfchain.plaoc.dweb.systemui.js.VirtualKeyboardFFI
import org.bfchain.plaoc.dweb.topbar.DWebTopBar
import org.bfchain.plaoc.dweb.topbar.TopBarFFI
import org.bfchain.plaoc.dweb.topbar.TopBarState
import org.bfchain.plaoc.dweb.urlscheme.CustomUrlScheme
import org.bfchain.plaoc.webkit.*
import java.net.URI
import kotlin.math.min


private const val TAG = "DWebView"

private const val LEAVE_URI_SYMBOL = ":~:dweb=leave";

@ExperimentalLayoutApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("JavascriptInterface")
@Composable
fun DWebView(
    state: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity,
    customUrlScheme: CustomUrlScheme? = null,
    modifier: Modifier = Modifier,
    onCreated: (AdAndroidWebView) -> Unit = {},
) {

    var jsUtil by remember(state) {
        mutableStateOf<JsUtil?>(null)
    }
    val hook = remember {
        AdWebViewHook()
    }
    val adNavigator = rememberAdWebViewNavigator()
    val adCaptureBackPresses by remember {
        mutableStateOf(true)
    }

    val doBack = state.content.getCurrentUrl()?.endsWith(LEAVE_URI_SYMBOL) == true
    BackHandler(
        // 如果要执行doBack，那么要禁用拦截
        !doBack
                // 如果有js上下文
                and
                (jsUtil != null)
                // 并且没有历史记录了，说明返回按钮会触发"退出行为"
                and
                !(adCaptureBackPresses && adNavigator.canGoBack)
    ) {
        // 这种 location.replace 行为不会触发 Navigator 的长度发生变化的同时，还能自动触发 onbeforeunload
        // @TODO 这里的风险在于，如果js代码卡住，那么这段代码会无法正常执行，那么就永远无法退出
        jsUtil!!.evalQueue("leave_page") { "location.replace(location.href+'#$LEAVE_URI_SYMBOL')" }
    }
    if (doBack) {
        SideEffect {
            activity.onBackPressed()
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SetTaskDescription(state, activity)
    }

    val systemUIState = SystemUIState.Default(activity)

    jsUtil?.apply {
        VirtualKeyboardFFI.injectVirtualKeyboardVars(
            this,
            LocalDensity.current, LocalLayoutDirection.current,
            systemUIState.virtualKeyboard.overlay.value, WindowInsets.ime,
            systemUIState.navigationBar.overlay.value, WindowInsets.navigationBars,
        )
    }


    var overlayOffset = IntOffset(0, 0);
    val overlayPadding = WindowInsets(0).let {
        var res = it;
        if (!systemUIState.statusBar.overlay.value) {
            res = res.add(WindowInsets.statusBars)
        }

        if (!systemUIState.virtualKeyboard.overlay.value && WindowInsets.isImeVisible) {
//            it.add(WindowInsets.ime) // ime本身就包含了navigationBars的高度
            overlayOffset =
                IntOffset(
                    0, min(
                        0, -WindowInsets.ime.getBottom(LocalDensity.current)
                                + WindowInsets.navigationBars.getBottom(LocalDensity.current)
                    )
                )
        } else if (!systemUIState.navigationBar.overlay.value) {
            res = res.add(WindowInsets.navigationBars)
        }
        res
    }.asPaddingValues()

    Log.i(TAG, "overlayPadding:$overlayPadding")


    val presseBack = remember {
        return@remember {
            activity.runOnUiThread {
                activity.onBackPressed()
            }
        }
    }

    val topBarState = TopBarState.Default(presseBack)

    @Composable
    fun TopAppBar() {
        DWebTopBar(jsUtil, state, topBarState)
    }


    val bottomBarState = BottomBarState.Default()

    @Composable
    fun BottomAppBar() {
        DWebBottomBar(jsUtil, bottomBarState)
    }

    Scaffold(
        modifier = modifier
            .padding(overlayPadding)
            .offset { overlayOffset },
        topBar = { if (!topBarState.overlay.value and topBarState.enabled.value) TopAppBar() },
        bottomBar = { if (!bottomBarState.overlay.value and bottomBarState.isEnabled) BottomAppBar() },
        content = { innerPadding ->
            DWebBackground(innerPadding, hook, activity)

            Log.i(TAG, "innerPadding:${innerPadding}");


            val jsAlertConfig = remember {
                mutableStateOf<JsAlertConfiguration?>(null)
            }

            val jsPromptConfig = remember {
                mutableStateOf<JsPromptConfiguration?>(null)
            }

            val jsConfirmConfig = remember {
                mutableStateOf<JsConfirmConfiguration?>(null)
            }

            val jsBeforeUnloadConfig = remember {
                mutableStateOf<JsConfirmConfiguration?>(null)
            }

            AdWebView(
                state = state,
                navigator = adNavigator,
                captureBackPresses = adCaptureBackPresses,
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
                        jsUtil!!,
                        systemUIState,
                    )
                    webView.addJavascriptInterface(systemUiFFI, "system_ui")
                    webView.addJavascriptInterface(systemUiFFI.virtualKeyboard, "virtual_keyboard")

                    val topBarFFI = TopBarFFI(
                        topBarState,
                    )
                    webView.addJavascriptInterface(topBarFFI, "top_bar")


                    val bottomBarFFI = BottomBarFFI(bottomBarState)
                    webView.addJavascriptInterface(bottomBarFFI, "bottom_bar")

                    val dialogFFI = DialogFFI(
                        jsUtil!!,
                        jsAlertConfig,
                        jsPromptConfig,
                        jsConfirmConfig,
                        jsBeforeUnloadConfig
                    )
                    webView.addJavascriptInterface(dialogFFI, "native_dialog")

                    onCreated(webView)
                },
                chromeClient = remember {
                    class MyWebChromeClient : AdWebChromeClient() {
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

                        private fun getJsDialogTitle(url: String?, label: String): String {
                            var title = state.pageTitle
                            try {
                                if (url != null) {
                                    val host = URI(url).host
                                    if (host.isNotEmpty()) {
                                        title = host
                                    }
                                }
                            } catch (e: Exception) {
                            }
                            return "$title $label"
                        }

                        private fun getJsDialogConfirmText() = "确认"
                        private fun getJsDialogCancelText() = "取消"

                        override fun onJsAlert(
                            view: WebView?,
                            url: String?,
                            message: String?,
                            result: JsResult?
                        ): Boolean {
                            if (result == null) {
                                return super.onJsAlert(view, url, message, result)
                            }
                            jsAlertConfig.value = JsAlertConfiguration(
                                getJsDialogTitle(url, "显示"),
                                message ?: "",
                                getJsDialogConfirmText(),
                            ).bindCallback(result)
                            return true
                        }

                        override fun onJsPrompt(
                            view: WebView?,
                            url: String?,
                            message: String?,
                            defaultValue: String?,
                            result: JsPromptResult?
                        ): Boolean {
                            if (result == null) {
                                return super.onJsPrompt(view, url, message, defaultValue, result)
                            }
                            jsPromptConfig.value = JsPromptConfiguration(
                                getJsDialogTitle(url, "填入"),
                                message ?: "",
                                defaultValue ?: "",
                                getJsDialogConfirmText(),
                                getJsDialogCancelText(),
                            ).bindCallback(result)
                            return true
                        }

                        override fun onJsConfirm(
                            view: WebView?,
                            url: String?,
                            message: String?,
                            result: JsResult?
                        ): Boolean {
                            if (result == null) {
                                return super.onJsConfirm(view, url, message, result)
                            }
                            jsConfirmConfig.value = JsConfirmConfiguration(
                                getJsDialogTitle(url, "询问"),
                                message ?: "",
                                getJsDialogConfirmText(),
                                getJsDialogCancelText(),
                            ).bindCallback(result)
                            return true
                        }

                        override fun onJsBeforeUnload(
                            view: WebView?,
                            url: String?,
                            message: String?,
                            result: JsResult?
                        ): Boolean {
                            if (result == null) {
                                return super.onJsBeforeUnload(view, url, message, result)
                            }
                            jsBeforeUnloadConfig.value = JsConfirmConfiguration(
                                getJsDialogTitle(url, "提示您"),
                                message ?: "",
                                "离开",
                                "留下",
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ).bindCallback(result)
                            return true
                        }

                    }
                    MyWebChromeClient()
                },
                client = remember {
                    class MyWebViewClient : AdWebViewClient() {
                        private val ITAG = "$TAG/CUSTOM-SCHEME"
                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            Log.i(ITAG, "Intercept Request: ${request?.url}")
                            if (request !== null && customUrlScheme?.isMatch(request) == true) {
                                return customUrlScheme.handleRequest(request)
                            }
                            return super.shouldInterceptRequest(view, request)
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            Log.i(ITAG, "Override Url Loading: ${request?.url}")
                            if (request !== null && customUrlScheme?.isCrossDomain(request) == true) {
                                return false
                            }
                            return super.shouldOverrideUrlLoading(view, request)
                        }
                    }
                    MyWebViewClient()
                },
                modifier = Modifier.let { m ->
                    var top = innerPadding.calculateTopPadding()
                    var bottom = innerPadding.calculateBottomPadding()
                    val layoutDirection = LocalLayoutDirection.current;
                    var start = innerPadding.calculateStartPadding(layoutDirection)
                    var end = innerPadding.calculateEndPadding(layoutDirection)
                    if (topBarState.overlay.value or !topBarState.enabled.value) {
                        top = 0.dp;
                    }
                    if (bottomBarState.overlay.value or !bottomBarState.isEnabled) {
                        bottom = 0.dp
                    }
                    if ((top.value == 0F) and (bottom.value == 0F)) {
                        start = 0.dp; end = 0.dp;
                    }
                    Log.i(TAG, "webview-padding $start, $top, $end, $bottom")
                    m.padding(start, top, end, bottom)
                },
            )

            //<editor-fold desc="Native UI">

            if (topBarState.overlay.value and topBarState.enabled.value) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = if (systemUIState.statusBar.overlay.value) {
                        with(LocalDensity.current) {
                            Modifier.offset(y = WindowInsets.statusBars.getTop(this).toDp())
                        }
                    } else {
                        Modifier
                    }
                ) {
                    TopAppBar()
                }
            }
            if (bottomBarState.overlay.value and bottomBarState.isEnabled) {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxSize().let {
                        it
                    }
                ) { BottomAppBar() }
            }

            jsAlertConfig.value?.openAlertDialog { jsAlertConfig.value = null }
            jsPromptConfig.value?.openPromptDialog { jsPromptConfig.value = null }
            jsConfirmConfig.value?.openConfirmDialog { jsConfirmConfig.value = null }
            jsBeforeUnloadConfig.value?.openBeforeUnloadDialog { jsBeforeUnloadConfig.value = null }

            //</editor-fold>
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


@androidx.annotation.RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
inline fun SetTaskDescription(state: AdWebViewState, activity: ComponentActivity) {
    var pageTitle by remember {
        mutableStateOf(state.pageTitle)
    }
    var pageIcon by remember {
        mutableStateOf(state.pageIcon)
    }
    if (pageTitle != state.pageTitle || pageIcon != state.pageIcon) {
        pageTitle = state.pageTitle
        pageIcon = state.pageIcon
        activity.runOnUiThread {
            activity.setTaskDescription(
                ActivityManager.TaskDescription(
                    pageTitle, pageIcon
                )
            )
        }
    }
}