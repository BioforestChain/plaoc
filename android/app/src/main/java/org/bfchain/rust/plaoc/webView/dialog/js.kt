package org.bfchain.rust.plaoc.webView.dialog

import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState
import org.bfchain.rust.plaoc.webView.jsutil.*

@Suppress("ObjectPropertyName")
class DialogFFI(
    private val jsUtil: JsUtil,
    private val alertConfig: MutableState<JsAlertConfiguration?>,
    private val promptConfig: MutableState<JsPromptConfiguration?>,
    private val confirmConfig: MutableState<JsConfirmConfiguration?>,
    private val beforeUnloadConfig: MutableState<JsConfirmConfiguration?>,
) {
    companion object {
        private val _alert_gson = JsAlertConfiguration._gson
        private val _prompt_gson = JsPromptConfiguration._gson
        private val _confirm_gson = JsConfirmConfiguration._gson
    }

    @JavascriptInterface
    fun openAlert(config: DataString<JsAlertConfiguration>, cb: CallbackString) {
        alertConfig.value = config.toData(JsAlertConfiguration::class.java).bindCallback {
            cb.callJs("dialog_ffi-alert", jsUtil)
        }
    }

    @JavascriptInterface
    fun openPrompt(config: DataString<JsPromptConfiguration>, cb: CallbackString) {
        promptConfig.value = config.toData(JsPromptConfiguration::class.java).bindCallback({
            cb.callJs("dialog_ffi-prompt", jsUtil, JsUtil.gson.toJson(it))
        }, {
            cb.callJs("dialog_ffi-prompt", jsUtil, "null")
        })
    }

    @JavascriptInterface
    fun openConfirm(config: DataString<JsConfirmConfiguration>, cb: CallbackString) {
        confirmConfig.value = config.toData(JsConfirmConfiguration::class.java).bindCallback({
            cb.callJs("dialog_ffi-confirm", jsUtil, "true")
        }, {
            cb.callJs("dialog_ffi-confirm", jsUtil, "false")
        })
    }

    @JavascriptInterface
    fun openBeforeUnload(config: DataString<JsConfirmConfiguration>, cb: CallbackString) {
        beforeUnloadConfig.value = config.toData(JsConfirmConfiguration::class.java).bindCallback({
            cb.callJs("dialog_ffi-before_unload", jsUtil, "true")
        }, {
            cb.callJs("dialog_ffi-before_unload", jsUtil, "false")
        })
    }
}

