package org.bfchain.rust.plaoc.webView.dialog

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState
import org.bfchain.rust.plaoc.webView.jsutil.*
private val TAG = "DialogFFI";
@Suppress("ObjectPropertyName")
class DialogFFI(
    private val jsUtil: JsUtil,
    private val alertConfig: MutableState<JsAlertConfiguration?>,
    private val promptConfig: MutableState<JsPromptConfiguration?>,
    private val confirmConfig: MutableState<JsConfirmConfiguration?>,
    private val warringConfig: MutableState<JsConfirmConfiguration?>,
) {
    companion object {
        private val _alert_gson = JsAlertConfiguration._gson
        private val _prompt_gson = JsPromptConfiguration._gson
        private val _confirm_gson = JsConfirmConfiguration._gson
    }

    @JavascriptInterface
    fun openAlert(config: DataString<JsAlertConfiguration>, cb: CallbackString) {
//      Log.i(TAG,"openAlert1:${config}")
        alertConfig.value = config.toData(JsAlertConfiguration::class.java).bindCallback {
            cb.callJs("dialog_ffi-alert", jsUtil)
        }
    }

    @JavascriptInterface
    fun openPrompt(config: DataString<JsPromptConfiguration>, cb: CallbackString) {
      Log.i(TAG,"openPrompt:${config}")
        promptConfig.value = config.toData(JsPromptConfiguration::class.java).bindCallback({
//          Log.i(TAG,"openPrompt1:${it}")
            cb.callJs("dialog_ffi-prompt", jsUtil, JsUtil.gson.toJson(it))
        }, {
          Log.i(TAG,"openPrompt2:${jsUtil}")
            cb.callJs("dialog_ffi-prompt", jsUtil, "null")
        })
    }

    @JavascriptInterface
    fun openConfirm(config: DataString<JsConfirmConfiguration>, cb: CallbackString) {
      Log.i(TAG,"openConfirm:${config}")
        confirmConfig.value = config.toData(JsConfirmConfiguration::class.java).bindCallback({
            cb.callJs("dialog_ffi-confirm", jsUtil, "true")
        }, {
            cb.callJs("dialog_ffi-confirm", jsUtil, "false")
        })
    }

    @JavascriptInterface
    fun openWarning(config: DataString<JsConfirmConfiguration>, cb: CallbackString) {
//      Log.i(TAG,"openWarring:${config}")
      warringConfig.value = config.toData(JsConfirmConfiguration::class.java).bindCallback({
            cb.callJs("dialog_ffi-warring", jsUtil, "true")
        }, {
            cb.callJs("dialog_ffi-warring", jsUtil, "false")
        })
    }
}

