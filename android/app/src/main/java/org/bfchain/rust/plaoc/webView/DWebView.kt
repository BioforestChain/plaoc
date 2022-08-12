package org.bfchain.rust.plaoc.webView

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Build
import android.os.Message
import android.util.Log
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavController
import org.bfchain.rust.plaoc.webView.bottombar.BottomBarFFI
import org.bfchain.rust.plaoc.webView.bottombar.BottomBarState
import org.bfchain.rust.plaoc.webView.bottombar.DWebBottomBar
import org.bfchain.rust.plaoc.webView.dialog.*
import org.bfchain.rust.plaoc.webView.jsutil.JsUtil
import org.bfchain.rust.plaoc.webView.navigator.NavigatorFFI
import org.bfchain.rust.plaoc.webView.network.*
import org.bfchain.rust.plaoc.webView.systemui.SystemUIState
import org.bfchain.rust.plaoc.webView.systemui.SystemUiFFI
import org.bfchain.rust.plaoc.webView.systemui.js.VirtualKeyboardFFI
import org.bfchain.rust.plaoc.webView.topbar.DWebTopBar
import org.bfchain.rust.plaoc.webView.topbar.TopBarFFI
import org.bfchain.rust.plaoc.webView.topbar.TopBarState
import org.bfchain.rust.plaoc.webView.urlscheme.CustomUrlScheme
import org.bfchain.rust.plaoc.webkit.*
import java.net.URI
import java.net.URL
import kotlin.math.min


private const val TAG = "DWebView"


private const val LEAVE_URI_SYMBOL = ":~:dweb=leave"

@ExperimentalLayoutApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("JavascriptInterface")
@Composable
fun DWebView(
    state: AdWebViewState,
    navController: NavController,
    activity: ComponentActivity,
    customUrlScheme: CustomUrlScheme,
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
        doBack
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

    var overlayOffset = IntOffset(0, 0)
    val overlayPadding = WindowInsets(0).let {
        var res = it
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
                    webView.setBackgroundColor(Companion.Transparent.toArgb())
                    webView.adWebViewHook = hook
                    // 通用的工具类
                    jsUtil = JsUtil(activity, evaluateJavascript = { code, callback ->
                        webView.evaluateJavascript(
                            code,
                            callback
                        )
                    })
                    val navigatorFFI = NavigatorFFI(webView, activity, navController)
                    webView.addJavascriptInterface(navigatorFFI, "my_nav")

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
                                    return true
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

                        // API >= 21
                        @SuppressLint("NewApi")
                        @Override
                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            Log.i(ITAG, "Intercept Request: ${request?.url}")
                            if (request !== null) {
                                // 这里出来的url全部都用是小写，俺觉得这是个bug
                                val url = request.url.toString()
                                // 拦截，跳过本地和远程脚本
                                if (jumpWhitelist(url)) {
                                    try {
                                        // 拦截视图文件
                                        if (url.endsWith(".html")) {
                                            return viewGateWay(customUrlScheme, request)
                                        }
                                        /**
                                         * 这里放行了所有的资源文件（比如 .css .gif .js) 只有api类型的数据会被拦截到
                                         * 下面这种实现方法会有安全问题（比如说一些.php,.jsp的提权），可能需要完善一下下面规则的健壮性。
                                         */
                                        val temp = url.substring(url.lastIndexOf("/") + 1)
                                        if (temp.startsWith("poll")) {
                                            return messageGateWay(request)
                                        }
                                        val suffixIndex = temp.lastIndexOf(".")
                                        // 只拦截数据文件,忽略资源文件
                                        if (suffixIndex == -1) {
                                            return dataGateWay(request)
                                        }
                                        // 映射本地文件的资源文件 https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/index.mjs -> /plaoc/index.mjs
                                        if (Regex(dWebView_host).containsMatchIn(url)) {
                                            val path = URL(url).path
                                            return customUrlScheme.handleRequest(request, path)
                                        }
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            return super.shouldInterceptRequest(view, request)
                        }

                        // API < 21
//                        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
//                            if (url != null) {
//                                if (!(url.startsWith("http://127.0.0.1") || url.startsWith(
//                                        "https://unpkg.com"
//                                    ))
//                                ) {
//                                    return super.shouldInterceptRequest(view,
//                                        url.let { gateWay(it) })
//                                }
//                            }
//                            return super.shouldInterceptRequest(view, url)
//                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            Log.i(ITAG, "Override Url Loading: ${request?.url}")
                            if (request !== null && customUrlScheme.isCrossDomain(request)) {
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
                    val layoutDirection = LocalLayoutDirection.current
                    var start = innerPadding.calculateStartPadding(layoutDirection)
                    var end = innerPadding.calculateEndPadding(layoutDirection)
                    if (topBarState.overlay.value or !topBarState.enabled.value) {
                        top = 0.dp
                    }
                    if (bottomBarState.overlay.value or !bottomBarState.isEnabled) {
                        bottom = 0.dp
                    }
                    if ((top.value == 0F) and (bottom.value == 0F)) {
                        start = 0.dp; end = 0.dp
                    }
                    Log.i(TAG, "webview-padding $start, $top, $end, $bottom")
                    m.padding(start, top, end, bottom)
                },
            )

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
        },
        containerColor = Companion.Transparent,
    )
}


private operator fun <T> Array<T>.component6(): Any {
    return this.elementAt(5) as Any
}


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