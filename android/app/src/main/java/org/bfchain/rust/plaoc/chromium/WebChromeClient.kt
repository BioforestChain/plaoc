package org.bfchain.rust.plaoc.chromium

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View;
import android.webkit.*
import org.chromium.android_webview.AwContentsClient.CustomViewCallback
import org.chromium.android_webview.AwContentsClient.FileChooserParamsImpl
import android.webkit.WebChromeClient.FileChooserParams
import org.chromium.android_webview.*
import org.chromium.android_webview.permission.AwPermissionRequest
import org.chromium.base.Callback

open class WebChromeClient {
    /**
     * Tell the host application the current progress of loading a page.
     * @param view The WebView that initiated the callback.
     * @param newProgress Current page loading progress, represented by
     *                    an integer between 0 and 100.
     */
    open fun onProgressChanged(view: WebView?, newProgress: Int): Unit {}

    /**
     * Notify the host application of a change in the document title.
     * @param view The WebView that initiated the callback.
     * @param title A String containing the new title of the document.
     */
    open fun onReceivedTitle(view: WebView?, title: String?): Unit {}

    /**
     * Notify the host application of a new favicon for the current page.
     * @param view The WebView that initiated the callback.
     * @param icon A Bitmap containing the favicon for the current page.
     */
    open fun onReceivedIcon(view: WebView?, icon: Bitmap?): Unit {}

    /**
     * Notify the host application of the url for an apple-touch-icon.
     * @param view The WebView that initiated the callback.
     * @param url The icon url.
     * @param precomposed {@code true} if the url is for a precomposed touch icon.
     */
    open fun onReceivedTouchIconUrl(view: WebView?, url: String, precomposed: Boolean): Unit {}

    /**
     * Notify the host application that the current page has entered full screen mode. After this
     * call, web content will no longer be rendered in the WebView, but will instead be rendered
     * in {@code view}. The host application should add this View to a Window which is configured
     * with {@link android.view.WindowManager.LayoutParams#FLAG_FULLSCREEN} flag in order to
     * actually display this web content full screen.
     *
     * <p>The application may explicitly exit fullscreen mode by invoking {@code callback} (ex. when
     * the user presses the back button). However, this is generally not necessary as the web page
     * will often show its own UI to close out of fullscreen. Regardless of how the WebView exits
     * fullscreen mode, WebView will invoke {@link #onHideCustomView()}, signaling for the
     * application to remove the custom View.
     *
     * <p>If this method is not overridden, WebView will report to the web page it does not support
     * fullscreen mode and will not honor the web page's request to run in fullscreen mode.
     *
     * <p class="note"><b>Note:</b> if overriding this method, the application must also override
     * {@link #onHideCustomView()}.
     *
     * @param view is the View object to be shown.
     * @param callback invoke this callback to request the page to exit
     * full screen mode.
     */
    open fun onShowCustomView(view: View, callback: CustomViewCallback): Unit {}

    /**
     * Notify the host application that the current page has exited full screen mode. The host
     * application must hide the custom View (the View which was previously passed to {@link
     * #onShowCustomView(View, CustomViewCallback) onShowCustomView()}). After this call, web
     * content will render in the original WebView again.
     *
     * <p class="note"><b>Note:</b> if overriding this method, the application must also override
     * {@link #onShowCustomView(View, CustomViewCallback) onShowCustomView()}.
     */
    open fun onHideCustomView(): Unit {}

    /**
     * Request the host application to create a new window. If the host
     * application chooses to honor this request, it should return {@code true} from
     * this method, create a new WebView to host the window, insert it into the
     * View system and send the supplied resultMsg message to its target with
     * the new WebView as an argument. If the host application chooses not to
     * honor the request, it should return {@code false} from this method. The default
     * implementation of this method does nothing and hence returns {@code false}.
     * <p>
     * Applications should typically not allow windows to be created when the
     * {@code isUserGesture} flag is false, as this may be an unwanted popup.
     * <p>
     * Applications should be careful how they display the new window: don't simply
     * overlay it over the existing WebView as this may mislead the user about which
     * site they are viewing. If your application displays the URL of the main page,
     * make sure to also display the URL of the new window in a similar fashion. If
     * your application does not display URLs, consider disallowing the creation of
     * new windows entirely.
     * <p class="note"><b>Note:</b> There is no trustworthy way to tell which page
     * requested the new window: the request might originate from a third-party iframe
     * inside the WebView.
     *
     * @param view The WebView from which the request for a new window
     *             originated.
     * @param isDialog {@code true} if the new window should be a dialog, rather than
     *                 a full-size window.
     * @param isUserGesture {@code true} if the request was initiated by a user gesture,
     *                      such as the user clicking a link.
     * @param resultMsg The message to send when once a new WebView has been
     *                  created. resultMsg.obj is a
     *                  {@link WebView.WebViewTransport} object. This should be
     *                  used to transport the new WebView, by calling
     *                  {@link WebView.WebViewTransport#setWebView(WebView)
     *                  WebView.WebViewTransport.setWebView(WebView)}.
     * @return This method should return {@code true} if the host application will
     *         create a new window, in which case resultMsg should be sent to
     *         its target. Otherwise, this method should return {@code false}. Returning
     *         {@code false} from this method but also sending resultMsg will result in
     *         undefined behavior.
     */
    open fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        return false
    }

    /**
     * Request display and focus for this WebView. This may happen due to
     * another WebView opening a link in this WebView and requesting that this
     * WebView be displayed.
     * @param view The WebView that needs to be focused.
     */
    open fun onRequestFocus(view: WebView?): Unit {}

    /**
     * Notify the host application to close the given WebView and remove it
     * from the view system if necessary. At this point, WebCore has stopped
     * any loading in this window and has removed any cross-scripting ability
     * in javascript.
     * <p>
     * As with {@link #onCreateWindow}, the application should ensure that any
     * URL or security indicator displayed is updated so that the user can tell
     * that the page they were interacting with has been closed.
     *
     * @param window The WebView that needs to be closed.
     */
    open fun onCloseWindow(window: WebView?): Unit {}

    /**
     * Notify the host application that the web page wants to display a
     * JavaScript {@code alert()} dialog.
     * <p>The default behavior if this method returns {@code false} or is not
     * overridden is to show a dialog containing the alert message and suspend
     * JavaScript execution until the dialog is dismissed.
     * <p>To show a custom dialog, the app should return {@code true} from this
     * method, in which case the default dialog will not be shown and JavaScript
     * execution will be suspended. The app should call
     * {@code JsResult.confirm()} when the custom dialog is dismissed such that
     * JavaScript execution can be resumed.
     * <p>To suppress the dialog and allow JavaScript execution to
     * continue, call {@code JsResult.confirm()} immediately and then return
     * {@code true}.
     * <p>Note that if the {@link WebChromeClient} is set to be {@code null},
     * or if {@link WebChromeClient} is not set at all, the default dialog will
     * be suppressed and Javascript execution will continue immediately.
     * <p>Note that the default dialog does not inherit the {@link
     * android.view.Display#FLAG_SECURE} flag from the parent window.
     *
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param result A JsResult to confirm that the user closed the window.
     * @return boolean {@code true} if the request is handled or ignored.
     * {@code false} if WebView needs to show the default dialog.
     */
    open fun onJsAlert(view: WebView?, url: String, message: String, result: JsResult): Boolean {
        return false
    }
    open fun handleJsAlert(view: WebView?, url: String, message: String, receiver: JsResultReceiver): Unit {
        receiver.cancel()
    }

    /**
     * Notify the host application that the web page wants to display a
     * JavaScript {@code confirm()} dialog.
     * <p>The default behavior if this method returns {@code false} or is not
     * overridden is to show a dialog containing the message and suspend
     * JavaScript execution until the dialog is dismissed. The default dialog
     * will return {@code true} to the JavaScript {@code confirm()} code when
     * the user presses the 'confirm' button, and will return {@code false} to
     * the JavaScript code when the user presses the 'cancel' button or
     * dismisses the dialog.
     * <p>To show a custom dialog, the app should return {@code true} from this
     * method, in which case the default dialog will not be shown and JavaScript
     * execution will be suspended. The app should call
     * {@code JsResult.confirm()} or {@code JsResult.cancel()} when the custom
     * dialog is dismissed.
     * <p>To suppress the dialog and allow JavaScript execution to continue,
     * call {@code JsResult.confirm()} or {@code JsResult.cancel()} immediately
     * and then return {@code true}.
     * <p>Note that if the {@link WebChromeClient} is set to be {@code null},
     * or if {@link WebChromeClient} is not set at all, the default dialog will
     * be suppressed and the default value of {@code false} will be returned to
     * the JavaScript code immediately.
     * <p>Note that the default dialog does not inherit the {@link
     * android.view.Display#FLAG_SECURE} flag from the parent window.
     *
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param result A JsResult used to send the user's response to
     *               javascript.
     * @return boolean {@code true} if the request is handled or ignored.
     * {@code false} if WebView needs to show the default dialog.
     */
    open fun onJsConfirm(view: WebView?, url: String, message: String, result: JsResult): Boolean {
        return false;
    }
    open fun handleJsConfirm(view: WebView?, url: String, message: String, receiver: JsResultReceiver): Unit {
        receiver.cancel()
    }

    /**
     * Notify the host application that the web page wants to display a
     * JavaScript {@code prompt()} dialog.
     * <p>The default behavior if this method returns {@code false} or is not
     * overridden is to show a dialog containing the message and suspend
     * JavaScript execution until the dialog is dismissed. Once the dialog is
     * dismissed, JavaScript {@code prompt()} will return the string that the
     * user typed in, or null if the user presses the 'cancel' button.
     * <p>To show a custom dialog, the app should return {@code true} from this
     * method, in which case the default dialog will not be shown and JavaScript
     * execution will be suspended. The app should call
     * {@code JsPromptResult.confirm(result)} when the custom dialog is
     * dismissed.
     * <p>To suppress the dialog and allow JavaScript execution to continue,
     * call {@code JsPromptResult.confirm(result)} immediately and then
     * return {@code true}.
     * <p>Note that if the {@link WebChromeClient} is set to be {@code null},
     * or if {@link WebChromeClient} is not set at all, the default dialog will
     * be suppressed and {@code null} will be returned to the JavaScript code
     * immediately.
     * <p>Note that the default dialog does not inherit the {@link
     * android.view.Display#FLAG_SECURE} flag from the parent window.
     *
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param defaultValue The default value displayed in the prompt dialog.
     * @param result A JsPromptResult used to send the user's reponse to
     *               javascript.
     * @return boolean {@code true} if the request is handled or ignored.
     * {@code false} if WebView needs to show the default dialog.
     */
    open fun onJsPrompt(view: WebView?, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
        return false
    }
    open fun handleJsPrompt(view: WebView?, url: String, message: String, defaultValue: String, receiver: JsPromptResultReceiver): Unit {
        receiver.cancel()
    }

    /**
     * Notify the host application that the web page wants to confirm navigation
     * from JavaScript {@code onbeforeunload}.
     * <p>The default behavior if this method returns {@code false} or is not
     * overridden is to show a dialog containing the message and suspend
     * JavaScript execution until the dialog is dismissed. The default dialog
     * will continue the navigation if the user confirms the navigation, and
     * will stop the navigation if the user wants to stay on the current page.
     * <p>To show a custom dialog, the app should return {@code true} from this
     * method, in which case the default dialog will not be shown and JavaScript
     * execution will be suspended. When the custom dialog is dismissed, the
     * app should call {@code JsResult.confirm()} to continue the navigation or,
     * {@code JsResult.cancel()} to stay on the current page.
     * <p>To suppress the dialog and allow JavaScript execution to continue,
     * call {@code JsResult.confirm()} or {@code JsResult.cancel()} immediately
     * and then return {@code true}.
     * <p>Note that if the {@link WebChromeClient} is set to be {@code null},
     * or if {@link WebChromeClient} is not set at all, the default dialog will
     * be suppressed and the navigation will be resumed immediately.
     * <p>Note that the default dialog does not inherit the {@link
     * android.view.Display#FLAG_SECURE} flag from the parent window.
     *
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param result A JsResult used to send the user's response to
     *               javascript.
     * @return boolean {@code true} if the request is handled or ignored.
     * {@code false} if WebView needs to show the default dialog.
     */
    open fun onJsBeforeUnload(view: WebView?, url: String, message: String, result: JsResult): Boolean {
        return false
    }
    open fun handleJsBeforeUnload(view: WebView?, url: String, message: String, receiver: JsResultReceiver): Unit {
        receiver.cancel()
    }

    /**
     * Notify the host application that web content from the specified origin
     * is attempting to use the Geolocation API, but no permission state is
     * currently set for that origin. The host application should invoke the
     * specified callback with the desired permission state. See
     * {@link GeolocationPermissions} for details.
     *
     * <p>Note that for applications targeting Android N and later SDKs
     * (API level > {@link android.os.Build.VERSION_CODES#M})
     * this method is only called for requests originating from secure
     * origins such as https. On non-secure origins geolocation requests
     * are automatically denied.
     *
     * @param origin The origin of the web content attempting to use the
     *               Geolocation API.
     * @param callback The callback to use to set the permission state for the
     *                 origin.
     */
    open fun onGeolocationPermissionsShowPrompt(origin: String, callback: AwGeolocationPermissions.Callback): Unit {}

    /**
     * Notify the host application that a request for Geolocation permissions,
     * made with a previous call to
     * {@link #onGeolocationPermissionsShowPrompt(String,GeolocationPermissions.Callback) onGeolocationPermissionsShowPrompt()}
     * has been canceled. Any related UI should therefore be hidden.
     */
    open fun onGeolocationPermissionsHidePrompt(): Unit {}

    /**
     * Notify the host application that web content is requesting permission to
     * access the specified resources and the permission currently isn't granted
     * or denied. The host application must invoke {@link PermissionRequest#grant(String[])}
     * or {@link PermissionRequest#deny()}.
     *
     * If this method isn't overridden, the permission is denied.
     *
     * @param request the PermissionRequest from current web content.
     */
    open fun onPermissionRequest(request: AwPermissionRequest): Unit {
        request.deny()
    }

    /**
     * Notify the host application that the given permission request
     * has been canceled. Any related UI should therefore be hidden.
     *
     * @param request the PermissionRequest that needs be canceled.
     */
    open fun onPermissionRequestCanceled(request: AwPermissionRequest): Unit {}

    /**
     * Report a JavaScript error message to the host application. The ChromeClient
     * should override this to process the log message as they see fit.
     * @param message The error message to report.
     * @param lineNumber The line number of the error.
     * @param sourceID The name of the source file that caused the error.
     * @deprecated Use {@link #onConsoleMessage(ConsoleMessage) onConsoleMessage(ConsoleMessage)}
     *      instead.
     */
    open fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String): Unit {}

    /**
     * Report a JavaScript console message to the host application. The ChromeClient
     * should override this to process the log message as they see fit.
     * @param consoleMessage Object containing details of the console message.
     * @return {@code true} if the message is handled by the client.
     */
    open fun onConsoleMessage(consoleMessage: AwConsoleMessage): Boolean {
        onConsoleMessage(consoleMessage.message(), consoleMessage.lineNumber(),
            consoleMessage.sourceId());
        return false;
    }

    /**
     * When not playing, video elements are represented by a 'poster' image. The
     * image to use can be specified by the poster attribute of the video tag in
     * HTML. If the attribute is absent, then a default poster will be used. This
     * method allows the ChromeClient to provide that default image.
     *
     * @return Bitmap The image to use as a default poster, or {@code null} if no such image is
     * available.
     */
    open fun getDefaultVideoPoster(): Bitmap? {
        return null
    }

    /**
     * Obtains a View to be displayed while buffering of full screen video is taking
     * place. The host application can override this method to provide a View
     * containing a spinner or similar.
     *
     * @return View The View to be displayed whilst the video is loading.
     */
    open fun getVideoLoadingProgressView(): View? {
        return null
    }

    /** Obtains a list of all visited history items, used for link coloring
     */
    open fun getVisitedHistory(callback: Callback<Array<String>>): Unit {}

    /**
     * Tell the client to show a file chooser.
     *
     * This is called to handle HTML forms with 'file' input type, in response to the
     * user pressing the "Select File" button.
     * To cancel the request, call <code>filePathCallback.onReceiveValue(null)</code> and
     * return {@code true}.
     *
     * @param webView The WebView instance that is initiating the request.
     * @param filePathCallback Invoke this callback to supply the list of paths to files to upload,
     *                         or {@code null} to cancel. Must only be called if the
     *                         {@link #onShowFileChooser} implementation returns {@code true}.
     * @param fileChooserParams Describes the mode of file chooser to be opened, and options to be
     *                          used with it.
     * @return {@code true} if filePathCallback will be invoked, {@code false} to use default
     *         handling.
     *
     * @see FileChooserParams
     */
    open fun onShowFileChooser(webView: WebView?, filePathCallback: Callback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
        return false
    }
    open fun showFileChooser(webView: WebView?, filePathCallback: Callback<Array<String>>, fileChooserParams: FileChooserParamsImpl): Unit {}
}
