package org.bfchain.rust.plaoc.webkit

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Message
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.utils.widget.MockView
import org.chromium.android_webview.*
import org.chromium.android_webview.test.AwTestContainerView
import org.chromium.android_webview.test.NullContentsClient
import org.chromium.base.Callback
import org.chromium.content_public.browser.ContentViewStatics
import org.chromium.content.browser.LauncherThread


open class ChromiumWebView(context: Context): FrameLayout(context) {
    private val awTestContainerView: AwTestContainerView
    private val awBrowserContext: AwBrowserContext
    private val awContents: AwContents
    private var webViewClient: WebViewClient? = null
    private var webChromeClient: WebChromeClient? = null

    // SQLiteDatabase instance
    private val httpAuthDatabase: HttpAuthDatabase =
        HttpAuthDatabase.newInstance(context, "bfs")

    var settings: AwSettings
        private set

//    constructor(context: Context): super(context)
//
//    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(
//        context, attrs, defStyleAttr
//    )

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
                    webViewClient?.onPageFinished(null, url)
                }
            }
        , settings)

        awTestContainerView.initialize(awContents)

        awTestContainerView.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        addView(awTestContainerView)

    }

    fun setHorizontalScrollbarOverlay(overlay: Boolean): Unit {
        awContents.setHorizontalScrollbarOverlay(overlay)
    }

    fun setVerticalScrollbarOverlay(overlay: Boolean): Unit {
        awContents.setVerticalScrollbarOverlay(overlay)
    }

    fun overlayHorizontalScrollbar(): Boolean {
        return awContents.overlayHorizontalScrollbar()
    }

    fun overlayVerticalScrollbar(): Boolean {
        return awContents.overlayVerticalScrollbar()
    }

    fun savePassword(host: String, username: String, password: String): Unit {
//        awContents.password
    }

    fun setHttpAuthUsernamePassword(host: String, realm: String, username: String, password: String): Unit {
        httpAuthDatabase.setHttpAuthUsernamePassword(host, realm, username, password)
    }

    fun getHttpAuthUsernamePassword(host: String, realm: String): Array<String> {
        return httpAuthDatabase.getHttpAuthUsernamePassword(host, realm)
    }

    fun hasHttpAuthUsernamePassword(): Boolean {
        return httpAuthDatabase.hasHttpAuthUsernamePassword()
    }

    fun clearHttpAuthUsernamePassword(): Unit {
        httpAuthDatabase.clearHttpAuthUsernamePassword()
    }

    fun destroy(): Unit = awContents.destroy()

    fun enablePlatformNotifications(): Unit {
        ContentViewStatics.enablePlatformNotifications()
    }

    fun disablePlatformNotifications(): Unit {
        ContentViewStatics.disablePlatformNotifications()
    }

    fun loadUrl(url: String): Unit = awContents.loadUrl(url)

    fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        awContents.loadUrl(url, additionalHttpHeaders)
    }

    fun loadData(data: String, mimeType: String, encoding: String): Unit {
        awContents.loadData(data, mimeType, encoding)
    }

    fun loadDataWithBaseURL(baseUrl: String?, data: String, mimeType: String?, encoding: String, failUrl: String?): Unit {
        awContents.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, failUrl)
    }

    fun stopLoading(): Unit = awContents.stopLoading()

    fun reload(): Unit = awContents.reload()

    fun canGoBack(): Boolean = awContents.canGoBack()

    fun goBack(): Unit = awContents.goBack()

    fun canGoForward(): Boolean = awContents.canGoForward()

    fun goForward(): Unit = awContents.goForward()

    fun canGoBackOrForward(steps: Int): Boolean = awContents.canGoBackOrForward(steps)

    fun goBackOrForward(steps: Int): Unit = awContents.goBackOrForward(steps)

    fun pageUp(top: Boolean): Boolean = awContents.pageUp(top)

    fun pageDown(bottom: Boolean): Boolean = awContents.pageDown(bottom)

    fun clearView(): Unit = awContents.clearView()

    fun capturePicture(): Picture = awContents.capturePicture()

    fun getScale(): Float = awContents.scale

    fun setInitialScale(scaleInPercent: Int): Unit {
        // todo 暂无设置方法
    }

    fun invokeZoomPicker(): Unit = awContents.invokeZoomPicker()

    fun requestFocusNodeHref(hrefMsg: Message): Unit = awContents.requestFocusNodeHref(hrefMsg)

    fun requestImageRef(msg: Message): Unit = awContents.requestImageRef(msg)

    fun getUrl(): String? {
        return awContents.url?.serialize() ?: null
    }

    fun getTitle(): String = awContents.title

    fun getFavicon(): Bitmap = awContents.favicon

    fun getProgress(): Int = awContents.mostRecentProgress

    fun getContentHeight(): Int = awContents.contentHeightCss

    fun pauseTimers(): Unit = awContents.pauseTimers()

    fun resumeTimers(): Unit = awContents.resumeTimers()

    fun onPause(): Unit = awContents.onPause()

    fun onResume(): Unit = awContents.onResume()

    fun isPaused(): Boolean = awContents.isPaused

    fun clearCache(includeDiskFiles: Boolean): Unit = awContents.clearCache(includeDiskFiles)

    fun clearFormData(): Unit = AwFormDatabase.clearFormData()

    fun clearHistory(): Unit = awContents.clearHistory()

    fun clearSslPreferences(): Unit = awContents.clearSslPreferences()

    fun documentHasImages(response: Message): Unit = awContents.documentHasImages(response)

    fun setWebViewClient(client: WebViewClient?): Unit {
        webViewClient = client
    }

    fun setDownloadListener(listener: DownloadListener): Unit {
        // todo 暂无
    }

    fun setWebChromeClient(client: WebChromeClient): Unit {
        webChromeClient = client
    }

    fun addJavascriptInterface(`object`: Any, name: String): Unit {
        awContents.addJavascriptInterface(`object`, name)
    }

    fun removeJavascriptInterface(name: String): Unit {
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
