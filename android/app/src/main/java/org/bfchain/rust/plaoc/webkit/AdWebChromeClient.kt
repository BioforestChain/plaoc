package org.bfchain.rust.plaoc.webkit

//import android.webkit.WebChromeClient
//import android.webkit.WebView
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Process
import android.util.Log
import android.webkit.ValueCallback
import org.bfchain.rust.plaoc.chromium.WebChromeClient
import org.bfchain.rust.plaoc.chromium.WebView
import org.bfchain.rust.plaoc.webkit.inputFile.AdFileInputHelper
import org.bfchain.rust.plaoc.webkit.inputFile.InputFileOptions
import org.chromium.android_webview.AwConsoleMessage
import org.chromium.android_webview.AwContentsClient.FileChooserParamsImpl
import org.chromium.base.Callback

/**
 * AccompanistWebChromeClient
 *
 * A parent class implementation of WebChromeClient that can be subclassed to add custom behaviour.
 *
 * As Accompanist Web needs to set its own web client to function, it provides this intermediary
 * class that can be overriden if further custom behaviour is required.
 */
open class AdWebChromeClient : WebChromeClient() {
    open lateinit var state: AdWebViewState
        internal set
    open lateinit var fileInputHelper: AdFileInputHelper
        internal set

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        state.pageTitle = title
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
        state.pageIcon = icon
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (state.loadingState is AdLoadingState.Finished) return
        state.loadingState = AdLoadingState.Loading(newProgress / 100.0f)
    }

    override fun showFileChooser(
        webView: WebView?,
        filePathCallback: Callback<Array<String>>,
        fileChooserParams: FileChooserParamsImpl
    ) {
//        super.showFileChooser(webView, filePathCallback, fileChooserParams)

        val callbackAdapter: ValueCallback<Array<Uri>> = object : ValueCallback<Array<Uri>> {
            private var mCompleted = false
            override fun onReceiveValue(uriList: Array<Uri>) {
                check(!mCompleted) { "showFileChooser result was already called" }
                mCompleted = true
                val s = arrayOf<String>()
                for (i in uriList.indices) {
                    s[i] = uriList[i].toString()
                }
                filePathCallback.onResult(s)
            }
        }

        var multiple = false
        var capture = false
        val accept = mutableListOf<String>()

        fileChooserParams.let { params ->
            capture = params.isCaptureEnabled
            multiple = params.mode == android.webkit.WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE
            accept.addAll(params.acceptTypes)
        }

        val launchFileInput = {
            fileInputHelper.filePathCallback = callbackAdapter
            val options =
                InputFileOptions(accept = accept, multiple = multiple, capture = capture)
            fileInputHelper.inputFileLauncher.launch(options)
        }

        launchFileInput()

        if (capture && PackageManager.PERMISSION_GRANTED != webView?.context?.checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Process.myPid(), Process.myUid()
            )
        ) {
            fileInputHelper.requestPermissionCallback = ValueCallback {
                launchFileInput()
            }
            fileInputHelper.requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            launchFileInput()
        }
    }

//    override fun showFileChooser(
//        webView: WebView?,
//        filePathCallback: Callback<Array<Uri>>,
//        fileChooserParams: FileChooserParamsImpl
//    ) {
////        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
//
//        var multiple = false
//        var capture = false
//        val accept = mutableListOf<String>()
//
//        fileChooserParams.let { params ->
//            capture = params.isCaptureEnabled
//            multiple = params.mode == android.webkit.WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE
//            accept.addAll(params.acceptTypes)
//        }
//
//        val launchFileInput = {
//            fileInputHelper.filePathCallback = filePathCallback
//            val options =
//                InputFileOptions(accept = accept, multiple = multiple, capture = capture)
//            fileInputHelper.inputFileLauncher.launch(options)
//        }
//
//        if (capture && PackageManager.PERMISSION_GRANTED != webView?.context?.checkPermission(
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Process.myPid(), Process.myUid()
//            ) ?: return false/*cancel*/
//        ) {
//            fileInputHelper.requestPermissionCallback = ValueCallback {
//                launchFileInput()
//            }
//            fileInputHelper.requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        } else {
//            launchFileInput()
//        }
//
//
//        return true
//    }

    override fun onConsoleMessage(consoleMessage: AwConsoleMessage): Boolean {
        consoleMessage.apply {
            Log.d("WebChromeClient", "${message()} -- From line ${lineNumber()} of ${sourceId()}")
        }
        return true
    }

    override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
        Log.d("MyApplication", "$message -- From line $lineNumber of $sourceID")
    }
}
