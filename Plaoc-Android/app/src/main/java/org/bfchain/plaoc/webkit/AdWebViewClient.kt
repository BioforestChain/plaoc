package org.bfchain.plaoc.webkit

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.accompanist.web.AdWebViewError

/**
 * AccompanistWebViewClient
 *
 * A parent class implementation of WebViewClient that can be subclassed to add custom behaviour.
 *
 * As Accompanist Web needs to set its own web client to function, it provides this intermediary
 * class that can be overriden if further custom behaviour is required.
 */
open class AdWebViewClient : WebViewClient() {
    open lateinit var state: AdWebViewState
        internal set
    open lateinit var navigator: AdWebViewNavigator
        internal set

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        state.loadingState = AdLoadingState.Loading(0.0f)
        state.errorsForCurrentRequest.clear()
        state.pageTitle = null
        state.pageIcon = null
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        state.loadingState = AdLoadingState.Finished
        navigator.canGoBack = view?.canGoBack() ?: false
        navigator.canGoForward = view?.canGoForward() ?: false
    }

    override fun doUpdateVisitedHistory(
        view: WebView?,
        url: String?,
        isReload: Boolean
    ) {
        super.doUpdateVisitedHistory(view, url, isReload)
        // WebView will often update the current url itself.
        // This happens in situations like redirects and navigating through
        // history. We capture this change and update our state holder url.
        // On older APIs (28 and lower), this method is called when loading
        // html data. We don't want to update the state in this case as that will
        // overwrite the html being loaded.
        if (url != null &&
            !url.startsWith("data:text/html") &&
            state.content.getCurrentUrl() != url
        ) {
            state.content = state.content.withUrl(url)
        }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)

        if (error != null) {
            state.errorsForCurrentRequest.add(AdWebViewError(request, error))
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        // Override all url loads to make the single source of truth
        // of the URL the state holder Url
        request?.let {
            state.content = state.content.withUrl(it.url.toString())
        }
        return true
    }
}