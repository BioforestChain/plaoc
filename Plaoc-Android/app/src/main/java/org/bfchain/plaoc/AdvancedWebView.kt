package org.bfchain.plaoc

/**
 * 参考 Android-AdvancedWebView 这个项目：
 * https://github.com/delight-im/Android-AdvancedWebView/blob/master/Source/library/src/main/java/im/delight/android/webview/AdvancedWebView.java
 *
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.webkit.GeolocationPermissions.Callback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.google.accompanist.web.*
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.nio.charset.Charset
import java.util.*


class AdvancedWebViewSettings {
    //    open lateinit var state: WebViewState
//        internal set
//    open lateinit var navigator: WebViewNavigator
//        internal set
    //<editor-fold desc="自定义监听器">
    val REQUEST_CODE_FILE_PICKER = 51426

    var mListener: Listener? = null
    var mActivity: WeakReference<Activity>? = null
    var mFragment: WeakReference<Fragment>? = null
    var mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER

    fun setListener(activity: Activity?, listener: Listener?) {
        setListener(activity, listener, REQUEST_CODE_FILE_PICKER)
    }

    fun setListener(activity: Activity?, listener: Listener?, requestCodeFilePicker: Int?) {
        if (activity != null) {
            mActivity = WeakReference<Activity>(activity)
        } else {
            mActivity = null
        }
        setListener(listener, requestCodeFilePicker)
    }

    fun setListener(fragment: Fragment?, listener: Listener?) {
        setListener(fragment, listener, REQUEST_CODE_FILE_PICKER)
    }

    fun setListener(fragment: Fragment?, listener: Listener?, requestCodeFilePicker: Int?) {
        if (fragment != null) {
            mFragment = WeakReference<Fragment>(fragment)
        } else {
            mFragment = null
        }
        setListener(listener, requestCodeFilePicker)
    }

    protected fun setListener(listener: Listener?, requestCodeFilePicker: Int?) {
        mListener = listener
        if (requestCodeFilePicker != null) {
            mRequestCodeFilePicker = requestCodeFilePicker
        }
    }


    protected var mLastError: Long = 0
    fun hasError(): Boolean {
        return (mLastError + 500) >= System.currentTimeMillis();
    }

    fun setLastError() {
        mLastError = System.currentTimeMillis()
    }
    //</editor-fold>

    //<editor-fold desc="白名单域名控制">
    protected val mPermittedHostnames: MutableList<String> = LinkedList<String>()

    fun addPermittedHostname(hostname: String) {
        mPermittedHostnames.add(hostname)
    }

    fun addPermittedHostnames(collection: Collection<String>) {
        mPermittedHostnames.addAll(collection)
    }

    fun getPermittedHostnames(): List<String?>? {
        return mPermittedHostnames
    }

    fun removePermittedHostname(hostname: String?) {
        mPermittedHostnames.remove(hostname)
    }

    fun clearPermittedHostnames() {
        mPermittedHostnames.clear()
    }

    fun isPermittedUrl(url: String?): Boolean {
        // if the permitted hostnames have not been restricted to a specific set
        if (mPermittedHostnames.size === 0) {
            // all hostnames are allowed
            return true
        }
        val parsedUrl: Uri = Uri.parse(url)

        // get the hostname of the URL that is to be checked
        val actualHost: String = parsedUrl.getHost() ?: return false

        // if the hostname could not be determined, usually because the URL has been invalid

        // if the host contains invalid characters (e.g. a backslash)
        if (!actualHost.matches(Regex("^[a-zA-Z0-9._!~*')(;:&=+$,%\\[\\]-]*$"))) {
            // prevent mismatches between interpretations by `Uri` and `WebView`, e.g. for `http://evil.example.com\.good.example.com/`
            return false
        }

        // get the user information from the authority part of the URL that is to be checked
        val actualUserInformation = parsedUrl.userInfo

        // if the user information contains invalid characters (e.g. a backslash)
        if (actualUserInformation != null && !actualUserInformation.matches(Regex("^[a-zA-Z0-9._!~*')(;:&=+$,%-]*$"))) {
            // prevent mismatches between interpretations by `Uri` and `WebView`, e.g. for `http://evil.example.com\@good.example.com/`
            return false
        }

        // for every hostname in the set of permitted hosts
        for (expectedHost in mPermittedHostnames) {
            // if the two hostnames match or if the actual host is a subdomain of the expected host
            if (actualHost == expectedHost || actualHost.endsWith(".$expectedHost")) {
                // the actual hostname of the URL to be checked is allowed
                return true
            }
        }

        // the actual hostname of the URL to be checked is not allowed since there were no matches
        return false
    }
    //</editor-fold>


    var mCustomWebViewClient: WebViewClient? = null

    fun setWebViewClient(client: WebViewClient) {
        mCustomWebViewClient = client
    }

    fun setWebChromeClient(client: WebChromeClient) {
        mCustomWebChromeClient = client
    }

    var mCustomWebChromeClient: WebChromeClient? = null

    //<editor-fold desc="多国语言">
    protected var mLanguageIso3: String = getLanguageIso3()
    protected val LANGUAGE_DEFAULT_ISO3 = "eng"
    protected fun getLanguageIso3(): String {
        return try {
            Locale.getDefault().isO3Language.lowercase(Locale.US)
        } catch (e: MissingResourceException) {
            LANGUAGE_DEFAULT_ISO3
        }
    }

    /**
     * Provides localizations for the 25 most widely spoken languages that have a ISO 639-2/T code
     *
     * @return the label for the file upload prompts as a string
     */
    protected fun getFileUploadPromptLabel(): String? {
        try {
            if (mLanguageIso3 == "zho") return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2") else if (mLanguageIso3 == "spa") return decodeBase64(
                "RWxpamEgdW4gYXJjaGl2bw=="
            ) else if (mLanguageIso3 == "hin") return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=") else if (mLanguageIso3 == "ben") return decodeBase64(
                "4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg="
            ) else if (mLanguageIso3 == "ara") return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==") else if (mLanguageIso3 == "por") return decodeBase64(
                "RXNjb2xoYSB1bSBhcnF1aXZv"
            ) else if (mLanguageIso3 == "rus") return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==") else if (mLanguageIso3 == "jpn") return decodeBase64(
                "MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA=="
            ) else if (mLanguageIso3 == "pan") return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=") else if (mLanguageIso3 == "deu") return decodeBase64(
                "V8OkaGxlIGVpbmUgRGF0ZWk="
            ) else if (mLanguageIso3 == "jav") return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=") else if (mLanguageIso3 == "msa") return decodeBase64(
                "UGlsaWggc2F0dSBmYWls"
            ) else if (mLanguageIso3 == "tel") return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=") else if (mLanguageIso3 == "vie") return decodeBase64(
                "Q2jhu41uIG3hu5l0IHThuq1wIHRpbg=="
            ) else if (mLanguageIso3 == "kor") return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=") else if (mLanguageIso3 == "fra") return decodeBase64(
                "Q2hvaXNpc3NleiB1biBmaWNoaWVy"
            ) else if (mLanguageIso3 == "mar") return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==") else if (mLanguageIso3 == "tam") return decodeBase64(
                "4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E="
            ) else if (mLanguageIso3 == "urd") return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==") else if (mLanguageIso3 == "fas") return decodeBase64(
                "2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA=="
            ) else if (mLanguageIso3 == "tur") return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==") else if (mLanguageIso3 == "ita") return decodeBase64(
                "U2NlZ2xpIHVuIGZpbGU="
            ) else if (mLanguageIso3 == "tha") return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH") else if (mLanguageIso3 == "guj") return decodeBase64(
                "4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY="
            )
        } catch (ignored: Exception) {
        }

        // return English translation by default
        return "Choose a file"
    }

    protected val CHARSET_DEFAULT = "UTF-8"

    @Throws(IllegalArgumentException::class, UnsupportedEncodingException::class)
    protected fun decodeBase64(base64: String?): String? {
        val bytes: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        return bytes.toString(Charset.forName(CHARSET_DEFAULT))
    }
    //</editor-fold>


    //<editor-fold desc="input[type=file]">
    /** File upload callback for platform versions prior to Android 5.0  */
    protected var mFileUploadCallbackFirst: ValueCallback<Uri?>? = null

    /** File upload callback for Android 5.0+  */
    protected var mFileUploadCallbackSecond: ValueCallback<Array<Uri>?>? = null
    protected var mUploadableFileTypes = "*/*"

    @SuppressLint("NewApi")
    fun openFileInput(
        fileUploadCallbackFirst: ValueCallback<Uri?>?,
        fileUploadCallbackSecond: ValueCallback<Array<Uri>?>?,
        allowMultiple: Boolean
    ) {
        mFileUploadCallbackFirst?.onReceiveValue(null)
        mFileUploadCallbackFirst = fileUploadCallbackFirst
        mFileUploadCallbackSecond?.onReceiveValue(null)
        mFileUploadCallbackSecond = fileUploadCallbackSecond
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        }
        i.type = mUploadableFileTypes
        if (mFragment != null && mFragment!!.get() != null && Build.VERSION.SDK_INT >= 11) {
            mFragment!!.get()!!.startActivityForResult(
                Intent.createChooser(i, getFileUploadPromptLabel()),
                mRequestCodeFilePicker
            )
        } else {
            mActivity?.get()?.let { activity ->
                activity.startActivityForResult(
                    Intent.createChooser(i, getFileUploadPromptLabel()),
                    mRequestCodeFilePicker
                )
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst!!.onReceiveValue(intent.data)
                        mFileUploadCallbackFirst = null
                    } else if (mFileUploadCallbackSecond != null) {
                        var dataUris: MutableList<Uri>? = null
                        try {
                            if (intent.dataString != null) {
                                dataUris = mutableListOf(Uri.parse(intent.dataString))
                            } else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.clipData != null) {
                                        val numSelectedFiles = intent.clipData!!.itemCount
                                        dataUris = mutableListOf<Uri>()
                                        for (i in 0 until numSelectedFiles) {
                                            dataUris.add(intent.clipData!!.getItemAt(i).uri)
                                        }
                                    }
                                }
                            }
                        } catch (ignored: java.lang.Exception) {
                        }
                        if (dataUris != null) {
                            mFileUploadCallbackSecond!!.onReceiveValue(dataUris.toTypedArray())
                            mFileUploadCallbackSecond = null
                        }
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst!!.onReceiveValue(null)
                    mFileUploadCallbackFirst = null
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond!!.onReceiveValue(null)
                    mFileUploadCallbackSecond = null
                }
            }
        }
    }

    /**
     * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
     *
     * @return whether file uploads can be used
     */
    fun isFileUploadAvailable(): Boolean {
        return isFileUploadAvailable(false)
    }

    /**
     * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
     *
     * On Android 4.4.3/4.4.4, file uploads may be possible but will come with a wrong MIME type
     *
     * @param needsCorrectMimeType whether a correct MIME type is required for file uploads or `application/octet-stream` is acceptable
     * @return whether file uploads can be used
     */
    fun isFileUploadAvailable(needsCorrectMimeType: Boolean): Boolean {
        return if (Build.VERSION.SDK_INT == 19) {
            val platformVersion = if (Build.VERSION.RELEASE == null) "" else Build.VERSION.RELEASE
            !needsCorrectMimeType && (platformVersion.startsWith("4.4.3") || platformVersion.startsWith(
                "4.4.4"
            ))
        } else {
            true
        }
    }


    //</editor-fold>

    var mGeolocationEnabled = false

}

class AdvancedWebViewClient(val mSettings: AdvancedWebViewSettings) : AccompanistWebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (!mSettings.hasError()) {
            mSettings.mListener?.onPageStarted?.let { it(url, favicon) }
        }
        mSettings.mCustomWebViewClient?.onPageStarted(view, url, favicon)
            ?: super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (!mSettings.hasError()) {
            mSettings.mListener?.onPageFinished?.let { it(url) }
        }
        mSettings.mCustomWebViewClient?.onPageFinished(view, url)
            ?: super.onPageFinished(view, url)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        mSettings.setLastError()
        mSettings.mListener?.onPageError?.let { it(request, error) }
        mSettings.mCustomWebViewClient?.onReceivedError(view, request, error)
            ?: super.onReceivedError(view, request, error)
    }


    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()
        if (!mSettings.isPermittedUrl(url)) {
            // if a listener is available
            // inform the listener about the request
            mSettings.mListener?.onExternalPageRequest?.let { it(url) }

            // cancel the original request
            return true
        }

        // if the user-specified handler asks to override the request
        if (mSettings.mCustomWebViewClient?.shouldOverrideUrlLoading(view, request)
                ?: super.shouldOverrideUrlLoading(view, request)
        ) {
            // cancel the original request
            return true
        }
        val uri: Uri = Uri.parse(url)
        val scheme = uri.scheme
        if (scheme != null) {
            val externalSchemeIntent: Intent?
            if (scheme == "tel") {
                externalSchemeIntent = Intent(Intent.ACTION_DIAL, uri)
            } else if (scheme == "sms") {
                externalSchemeIntent = Intent(Intent.ACTION_SENDTO, uri)
            } else if (scheme == "mailto") {
                externalSchemeIntent = Intent(Intent.ACTION_SENDTO, uri)
            } else if (scheme == "whatsapp") {
                externalSchemeIntent = Intent(Intent.ACTION_SENDTO, uri)
                externalSchemeIntent.setPackage("com.whatsapp")
            } else {
                externalSchemeIntent = null
            }
            if (externalSchemeIntent != null) {
                externalSchemeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    mSettings.mActivity?.get()?.startActivity(externalSchemeIntent)
                        ?: view?.context?.startActivity(externalSchemeIntent)
                } catch (ignored: ActivityNotFoundException) {
                }

                // cancel the original request
                return true
            }
        }

        // route the request through the custom URL loading method
        view?.loadUrl(url)

        // cancel the original request
        return true
    }

    override fun onLoadResource(view: WebView, url: String) {
        mSettings.mCustomWebViewClient?.onLoadResource(view, url)
            ?: super.onLoadResource(view, url)
    }

    @SuppressLint("NewApi")
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return if (Build.VERSION.SDK_INT >= 11) {
            mSettings.mCustomWebViewClient?.shouldInterceptRequest(view, request)
                ?: super.shouldInterceptRequest(view, request)
        } else {
            null
        }
    }

    override fun onFormResubmission(
        view: WebView?,
        dontResend: Message?,
        resend: Message?
    ) {
        mSettings.mCustomWebViewClient?.onFormResubmission(view, dontResend, resend)
            ?: super.onFormResubmission(view, dontResend, resend)
    }

    override fun doUpdateVisitedHistory(
        view: WebView?,
        url: String?,
        isReload: Boolean
    ) {
        mSettings.mCustomWebViewClient?.doUpdateVisitedHistory(view, url, isReload)
            ?: super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError
    ) {
        mSettings.mCustomWebViewClient?.onReceivedSslError(view, handler, error)
            ?: super.onReceivedSslError(view, handler, error)
    }

    @SuppressLint("NewApi")
    override fun onReceivedClientCertRequest(
        view: WebView,
        request: ClientCertRequest
    ) {
        if (Build.VERSION.SDK_INT >= 21) {
            mSettings.mCustomWebViewClient?.onReceivedClientCertRequest(view, request)
                ?: super.onReceivedClientCertRequest(view, request)
        }
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler,
        host: String,
        realm: String
    ) {
        mSettings.mCustomWebViewClient?.onReceivedHttpAuthRequest(
            view,
            handler,
            host,
            realm
        ) ?: super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return mSettings.mCustomWebViewClient?.shouldOverrideKeyEvent(view, event)
            ?: super.shouldOverrideKeyEvent(view, event)
    }


    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        mSettings.mCustomWebViewClient?.onUnhandledKeyEvent(view, event)
            ?: super.onUnhandledKeyEvent(view, event)
    }

//                @SuppressLint("NewApi")
//                fun onUnhandledInputEvent(view: WebView?, event: InputEvent?) {
//                    if (Build.VERSION.SDK_INT >= 21) {
//                            mSettings.mCustomWebViewClient?.onUnhandledInputEvent(view, event)?:super.onUnhandledInputEvent(view, event)
//                    }
//                }

    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        mSettings.mCustomWebViewClient?.onScaleChanged(view, oldScale, newScale)
            ?: super.onScaleChanged(view, oldScale, newScale)
    }

    @SuppressLint("NewApi")
    override fun onReceivedLoginRequest(
        view: WebView,
        realm: String,
        account: String?,
        args: String
    ) {
        if (Build.VERSION.SDK_INT >= 12) {
            mSettings.mCustomWebViewClient?.onReceivedLoginRequest(
                view,
                realm,
                account,
                args
            ) ?: super.onReceivedLoginRequest(view, realm, account, args)
        }
    }
};

class AdvancedWebChromeClient(val mSettings: AdvancedWebViewSettings) :
    AccompanistWebChromeClient() {

    var onShowFileChooser: ((
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) -> Boolean)? = null

    // file upload callback (Android 5.0 (API level 21) -- current) (public method)
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>?>?,
        fileChooserParams: FileChooserParams
    ): Boolean {
        val isHandled = mCustomWebChromeClient?.onShowFileChooser(
            webView,
            filePathCallback,
            fileChooserParams
        )
        if (isHandled == true) {
            return isHandled
        }
        return onShowFileChooser?.let {
            it(
                webView,
                filePathCallback,
                fileChooserParams
            )
        } ?: if (Build.VERSION.SDK_INT >= 21) {
            val allowMultiple = fileChooserParams.mode == FileChooserParams.MODE_OPEN_MULTIPLE
            mSettings.openFileInput(null, filePathCallback, allowMultiple)
            true
        } else {
            false
        }
    }


    private val mCustomWebChromeClient: WebChromeClient?
        get() = mSettings.mCustomWebChromeClient

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        mCustomWebChromeClient?.onProgressChanged(view, newProgress)
            ?: super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        mCustomWebChromeClient?.onReceivedTitle(view, title) ?: super.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        mCustomWebChromeClient?.onReceivedIcon(view, icon) ?: super.onReceivedIcon(view, icon)
    }

    override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
        mCustomWebChromeClient?.onReceivedTouchIconUrl(view, url, precomposed)
            ?: super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        mCustomWebChromeClient?.onShowCustomView(view, callback) ?: super.onShowCustomView(
            view,
            callback
        )
    }

    override fun onHideCustomView() {
        mCustomWebChromeClient?.onHideCustomView() ?: super.onHideCustomView()
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        return mCustomWebChromeClient?.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
            ?: super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onRequestFocus(view: WebView?) {
        mCustomWebChromeClient?.onRequestFocus(view) ?: super.onRequestFocus(view)
    }

    override fun onCloseWindow(window: WebView?) {
        mCustomWebChromeClient?.onCloseWindow(window) ?: super.onCloseWindow(window)
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return mCustomWebChromeClient?.onJsAlert(view, url, message, result) ?: super.onJsAlert(
            view,
            url,
            message,
            result
        )
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return mCustomWebChromeClient?.onJsConfirm(view, url, message, result) ?: super.onJsConfirm(
            view,
            url,
            message,
            result
        )
    }

    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        return mCustomWebChromeClient?.onJsPrompt(view, url, message, defaultValue, result)
            ?: super.onJsPrompt(view, url, message, defaultValue, result)
    }

    override fun onJsBeforeUnload(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return mCustomWebChromeClient?.onJsBeforeUnload(view, url, message, result)
            ?: super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: Callback) {
        if (mSettings.mGeolocationEnabled) {
            callback.invoke(origin, true, false)
        } else {
            mCustomWebChromeClient?.onGeolocationPermissionsShowPrompt(origin, callback)
                ?: super.onGeolocationPermissionsShowPrompt(origin, callback)
        }
    }

    override fun onGeolocationPermissionsHidePrompt() {
        mCustomWebChromeClient?.onGeolocationPermissionsHidePrompt()
            ?: super.onGeolocationPermissionsHidePrompt()
    }

    @SuppressLint("NewApi")
    override fun onPermissionRequest(request: PermissionRequest?) {
        if (Build.VERSION.SDK_INT >= 21) {
            mCustomWebChromeClient?.onPermissionRequest(request) ?: super.onPermissionRequest(
                request
            )
        }
    }

    @SuppressLint("NewApi")
    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        if (Build.VERSION.SDK_INT >= 21) {
            mCustomWebChromeClient?.onPermissionRequestCanceled(request)
                ?: super.onPermissionRequestCanceled(request)
        }
    }


    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return mCustomWebChromeClient?.onConsoleMessage(consoleMessage) ?: super.onConsoleMessage(
            consoleMessage
        )
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return mCustomWebChromeClient?.defaultVideoPoster ?: super.getDefaultVideoPoster()
    }

    override fun getVideoLoadingProgressView(): View? {
        return mCustomWebChromeClient?.videoLoadingProgressView
            ?: super.getVideoLoadingProgressView()
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String?>?>?) {
        mCustomWebChromeClient?.getVisitedHistory(callback) ?: super.getVisitedHistory(callback)
    }
}


var chromeFilePathCallback: ValueCallback<Array<Uri>?>? = null
fun runChromeFilePathCallback(uriList: List<Uri>) {
    if (uriList.isNotEmpty()) {
        chromeFilePathCallback?.onReceiveValue(uriList.toTypedArray())
    } else {
        chromeFilePathCallback?.onReceiveValue(null)
    }
    chromeFilePathCallback = null
}

private const val TAG = "AdvancedWebView"

@SuppressLint("JavascriptInterface")
@Composable
fun AdvancedWebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    captureBackPresses: Boolean = true,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    onCreated: (WebView) -> Unit = {},
    mSettings: AdvancedWebViewSettings = remember { AdvancedWebViewSettings() },
) {

    data class InputFileOptions(
        val accept: List<String>,
        val multiple: Boolean,
        val capture: Boolean
    ) {

    }

    @RequiresApi(18)
    open class InputFile :
        ActivityResultContract<InputFileOptions, List<@JvmSuppressWildcards Uri>>() {
        @CallSuper
        override fun createIntent(context: Context, input: InputFileOptions): Intent {
            return Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE).also { it ->
                    if (input.multiple) {
                        it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                    /**
                     * @todo Android EXTRA_MIME_TYPES 不支持 HTML-accpet标准中的文件名后缀:
                     *
                     * - A valid case-insensitive filename extension, starting with a period (".") character. For example: .jpg, .pdf, or .doc.
                     * - A valid MIME type string, with no extensions.
                     * - The string audio/\* meaning "any audio file".
                     * - The string video/\* meaning "any video file".
                     * - The string image/\* meaning "any image file".
                     */
                    if (input.accept.size > 1) {
                        it.type = "*/*"
                        it.putExtra(Intent.EXTRA_MIME_TYPES, input.accept.toTypedArray())
                    } else {
                        it.type = input.accept.getOrElse(0) { "*/*" }
                    }
                    /**
                     * @todo 增加 capture 的支持
                     */
                }

        }

        final override fun getSynchronousResult(
            context: Context,
            input: InputFileOptions
        ): SynchronousResult<List<@JvmSuppressWildcards Uri>>? = null

        final override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
            return intent.takeIf {
                resultCode == Activity.RESULT_OK
            }?.let {
                // Use a LinkedHashSet to maintain any ordering that may be
                // present in the ClipData
                val resultSet = LinkedHashSet<Uri>()
                it.data?.let { data ->
                    resultSet.add(data)
                }
                val clipData = it.clipData
                if (clipData == null && resultSet.isEmpty()) {
                    return emptyList()
                } else if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        if (uri != null) {
                            resultSet.add(uri)
                        }
                    }
                }
                return ArrayList(resultSet)
            } ?: emptyList()
        }
    }


    val inputFileLauncher = rememberLauncherForActivityResult(
        contract = InputFile(),
        onResult = { result ->
            Log.i(TAG, "InputFile Result:$result")
            runChromeFilePathCallback(result)
        });

    val context = LocalContext.current

    WebView(
        state = state,
        modifier = modifier,
        captureBackPresses = captureBackPresses,
        navigator = navigator,
        onCreated = { webView ->
            webView.settings.javaScriptEnabled = true;
            webView.settings.setSupportMultipleWindows(true);
            webView.settings.allowFileAccess = true;
            webView.settings.javaScriptCanOpenWindowsAutomatically = true;
            webView.settings.domStorageEnabled = true;
            webView.settings.databaseEnabled = true;
            webView.settings.safeBrowsingEnabled = true;
            webView.settings.setGeolocationEnabled(true);

            onCreated(webView)
        },
        client = remember {
            AdvancedWebViewClient(mSettings = mSettings)
        },
        chromeClient = remember {
            val wcc = AdvancedWebChromeClient(mSettings = mSettings)
            wcc.onShowFileChooser = { webView: WebView?,
                                      filePathCallback: ValueCallback<Array<Uri>?>?,
                                      fileChooserParams: WebChromeClient.FileChooserParams? ->
                var multiple = false
                var capture = false
                val accept = mutableListOf<String>();

                fileChooserParams?.let { params ->
                    capture = params.isCaptureEnabled
                    multiple = params.mode == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE
                    accept.addAll(params.acceptTypes)
                }
                chromeFilePathCallback = filePathCallback
                val options =
                    InputFileOptions(accept = accept, multiple = multiple, capture = capture)
                inputFileLauncher.launch(options)

                true
            }
            wcc
        }
    )
}


interface Listener {
    var onPageStarted: ((url: String?, favicon: Bitmap?) -> Unit)?
    var onPageFinished: ((url: String?) -> Unit)?

    var onPageError: ((
        request: WebResourceRequest?,
        error: WebResourceError?
    ) -> Unit)?

    var onDownloadRequested: ((
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) -> Unit)?

    var onExternalPageRequest: ((url: String?) -> Unit)?

}
