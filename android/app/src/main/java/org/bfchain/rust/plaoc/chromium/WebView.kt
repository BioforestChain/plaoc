package org.bfchain.rust.plaoc.chromium

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.webkit.DownloadListener
//import android.webkit.WebChromeClient
import android.webkit.WebView
//import android.webkit.WebViewClient
import android.widget.LinearLayout
import org.chromium.android_webview.*
import org.chromium.android_webview.test.AwTestContainerView
import org.chromium.android_webview.test.NullContentsClient
import org.chromium.base.Callback
import org.chromium.components.embedder_support.util.WebResourceResponseInfo
import org.chromium.content_public.browser.ContentViewStatics
import org.chromium.content.browser.LauncherThread


open class WebView: WebView {
    private val awTestContainerView: AwTestContainerView
    private val awBrowserContext: AwBrowserContext
    private val awContents: AwContents
    private var cwebViewClient: WebViewClient? = null
    private var cwebChromeClient: WebChromeClient? = null
    private var downloadListener: DownloadListener? = null

    // SQLiteDatabase instance
    private val httpAuthDatabase: HttpAuthDatabase =
        HttpAuthDatabase.newInstance(context, "bfs")

    var settings: AwSettings
        private set

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(
        context, attrs, defStyleAttr
    )

    init {
        AwTestContainerView.installDrawFnFunctionTable(false)
        awTestContainerView = AwTestContainerView(context, true)
        val sharedPreferences = context.getSharedPreferences(javaClass.simpleName, Context.MODE_PRIVATE)
        val nativePointer = AwBrowserContext.getDefault().nativePointer
        awBrowserContext = AwBrowserContext(sharedPreferences, nativePointer, true)

        settings = AwSettings(
            context,
            true,
            false,
            false,
            false,
            false
        ).apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        awContents = AwContents(
            awBrowserContext,
            awTestContainerView,
            awTestContainerView.context,
            awTestContainerView.internalAccessDelegate,
            awTestContainerView.nativeDrawFunctorFactory,
            object: NullContentsClient() {
                override fun onPageFinished(url: String) {
                    Log.i("ChromiumWebView", "onPageFinished")
                    cwebViewClient?.onPageFinished(null, url)
                }

                override fun shouldOverrideUrlLoading(request: AwWebResourceRequest): Boolean {
                    Log.i("ChromiumWebView", "shouldOverrideUrlLoading")
                    return cwebViewClient?.shouldOverrideUrlLoading(null, request) ?: false
                }

                override fun shouldInterceptRequest(request: AwWebResourceRequest): WebResourceResponseInfo? {
                    Log.i("ChromiumWebView", "shouldInterceptRequest")
                    return cwebViewClient?.shouldInterceptRequest(null, request)
                }
            }
            , settings)

        awTestContainerView.initialize(awContents)

        awTestContainerView.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        addView(awTestContainerView)

    }

    override fun setHorizontalScrollbarOverlay(overlay: Boolean): Unit {
        awContents.setHorizontalScrollbarOverlay(overlay)
    }

    override fun setVerticalScrollbarOverlay(overlay: Boolean): Unit {
        awContents.setVerticalScrollbarOverlay(overlay)
    }

    override fun overlayHorizontalScrollbar(): Boolean {
        return awContents.overlayHorizontalScrollbar()
    }

    override fun overlayVerticalScrollbar(): Boolean {
        return awContents.overlayVerticalScrollbar()
    }

    override fun savePassword(host: String, username: String, password: String): Unit {
//        awContents.password
    }

    override fun setHttpAuthUsernamePassword(host: String, realm: String, username: String, password: String): Unit {
        httpAuthDatabase.setHttpAuthUsernamePassword(host, realm, username, password)
    }

    override fun getHttpAuthUsernamePassword(host: String, realm: String): Array<String> {
        return httpAuthDatabase.getHttpAuthUsernamePassword(host, realm)
    }

    fun hasHttpAuthUsernamePassword(): Boolean {
        return httpAuthDatabase.hasHttpAuthUsernamePassword()
    }

    fun clearHttpAuthUsernamePassword(): Unit {
        httpAuthDatabase.clearHttpAuthUsernamePassword()
    }

    override fun destroy(): Unit = awContents.destroy()

    fun enablePlatformNotifications(): Unit {
        ContentViewStatics.enablePlatformNotifications()
    }

    fun disablePlatformNotifications(): Unit {
        ContentViewStatics.disablePlatformNotifications()
    }

    override fun loadUrl(url: String): Unit = awContents.loadUrl(url)

    override fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        awContents.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadData(data: String, mimeType: String?, encoding: String?): Unit {
        awContents.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(baseUrl: String?, data: String, mimeType: String?, encoding: String?, failUrl: String?): Unit {
        awContents.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, failUrl)
    }

    override fun stopLoading(): Unit = awContents.stopLoading()

    override fun reload(): Unit = awContents.reload()

    override fun canGoBack(): Boolean = awContents.canGoBack()

    override fun goBack(): Unit = awContents.goBack()

    override fun canGoForward(): Boolean = awContents.canGoForward()

    override fun goForward(): Unit = awContents.goForward()

    override fun canGoBackOrForward(steps: Int): Boolean = awContents.canGoBackOrForward(steps)

    override fun goBackOrForward(steps: Int): Unit = awContents.goBackOrForward(steps)

    override fun pageUp(top: Boolean): Boolean = awContents.pageUp(top)

    override fun pageDown(bottom: Boolean): Boolean = awContents.pageDown(bottom)

    override fun clearView(): Unit = awContents.clearView()

    override fun capturePicture(): Picture = awContents.capturePicture()

    override fun getScale(): Float = awContents.scale

    override fun setInitialScale(scaleInPercent: Int): Unit {
        // todo 暂无设置方法
    }

    override fun invokeZoomPicker(): Unit = awContents.invokeZoomPicker()

    override fun requestFocusNodeHref(hrefMsg: Message?): Unit = awContents.requestFocusNodeHref(hrefMsg)

    override fun requestImageRef(msg: Message): Unit = awContents.requestImageRef(msg)

    override fun getUrl(): String? {
        return awContents.url?.serialize() ?: null
    }

    override fun getTitle(): String = awContents.title

    override fun getFavicon(): Bitmap = awContents.favicon

    override fun getProgress(): Int = awContents.mostRecentProgress

    override fun getContentHeight(): Int = awContents.contentHeightCss

    override fun pauseTimers(): Unit = awContents.pauseTimers()

    override fun resumeTimers(): Unit = awContents.resumeTimers()

    override fun onPause(): Unit = awContents.onPause()

    override fun onResume(): Unit = awContents.onResume()

    fun isPaused(): Boolean = awContents.isPaused

    override fun clearCache(includeDiskFiles: Boolean): Unit = awContents.clearCache(includeDiskFiles)

    override fun clearFormData(): Unit = AwFormDatabase.clearFormData()

    override fun clearHistory(): Unit = awContents.clearHistory()

    override fun clearSslPreferences(): Unit = awContents.clearSslPreferences()

    override fun documentHasImages(response: Message): Unit = awContents.documentHasImages(response)

    fun setWebViewClient(client: WebViewClient): Unit {
        cwebViewClient = client
    }

    override fun setDownloadListener(listener: DownloadListener?): Unit {
        downloadListener = listener
    }

    fun setWebChromeClient(client: WebChromeClient?): Unit {
        cwebChromeClient = client
    }

    override fun addJavascriptInterface(`object`: Any, name: String): Unit {
        awContents.addJavascriptInterface(`object`, name)
    }

    override fun removeJavascriptInterface(name: String): Unit {
        awContents.removeJavascriptInterface(name)
    }

    fun evaluateJavascript(script: String, callback: Callback<String>?): Unit {
        awContents.evaluateJavaScript(script, callback)
    }

    companion object {
        fun findAddress(addr: String): String {
            return AwContentsStatics.findAddress(addr)
        }

        fun post(r: Runnable): Unit = LauncherThread.post(r)
    }
}
