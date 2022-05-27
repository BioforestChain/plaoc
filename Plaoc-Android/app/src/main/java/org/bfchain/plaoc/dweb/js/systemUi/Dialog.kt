package org.bfchain.plaoc.dweb.js.systemUi

import android.webkit.JavascriptInterface
import android.webkit.JsPromptResult
import android.webkit.JsResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties
import org.bfchain.plaoc.dweb.js.util.*

data class JsAlertConfiguration(
    val title: String,
    val content: String,
    val confirmText: String,
    val dismissOnBackPress: Boolean,
    val dismissOnClickOutside: Boolean,
) {
    companion object {
        operator fun invoke(
            title: String,
            content: String,
            confirmText: String,
            dismissOnBackPress: Boolean? = null,
            dismissOnClickOutside: Boolean? = null,
        ) = JsAlertConfiguration(
            title,
            content,
            confirmText,
            dismissOnBackPress ?: true,
            dismissOnClickOutside ?: false,
        )

        val _gson =
            JsUtil.registerGsonDeserializer(JsAlertConfiguration::class.java) { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                JsAlertConfiguration(
                    jsonObject["title"]?.asString ?: "",
                    jsonObject["content"]?.asString ?: "",
                    jsonObject["confirmText"]?.asString ?: "",
                    jsonObject["dismissOnBackPress"]?.asBoolean,
                    jsonObject["dismissOnClickOutside"]?.asBoolean,
                )
            }
    }

    private lateinit var onConfirm: () -> Unit
    fun bindCallback(onConfirm: () -> Unit): JsAlertConfiguration {
        this.onConfirm = onConfirm
        return this
    }

    fun bindCallback(result: JsResult): JsAlertConfiguration {
        bindCallback { result.confirm() }
        return this
    }

    @Composable
    fun openAlertDialog(requestDismiss: () -> Unit) {
        val config = this
        val closeAlert = {
            this.onConfirm()
            requestDismiss()
        }
        AlertDialog(
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Alert") },
            onDismissRequest = closeAlert,
            title = { Text(text = config.title) },
            text = { Text(text = config.content) },
            confirmButton = {
                TextButton(onClick = closeAlert) { Text(config.confirmText) }
            },
            properties = DialogProperties(
                dismissOnBackPress = config.dismissOnBackPress,
                dismissOnClickOutside = config.dismissOnClickOutside
            ),
        )
    }
}

data class JsPromptConfiguration(
    val title: String,
    val label: String,
    val defaultValue: String,
    val confirmText: String,
    val cancelText: String,
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = false,
) {
    companion object {
        operator fun invoke(
            title: String,
            label: String,
            defaultValue: String,
            confirmText: String,
            cancelText: String,
            dismissOnBackPress: Boolean? = null,
            dismissOnClickOutside: Boolean? = null,
        ) = JsPromptConfiguration(
            title,
            label,
            defaultValue,
            confirmText,
            cancelText,
            dismissOnBackPress ?: true,
            dismissOnClickOutside ?: false,
        )

        val _gson =
            JsUtil.registerGsonDeserializer(JsPromptConfiguration::class.java) { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                JsPromptConfiguration(
                    jsonObject["title"]?.asString ?: "",
                    jsonObject["content"]?.asString ?: "",
                    jsonObject["defaultValue"]?.asString ?: "",
                    jsonObject["confirmText"]?.asString ?: "",
                    jsonObject["cancelText"]?.asString ?: "",
                    jsonObject["dismissOnBackPress"]?.asBoolean,
                    jsonObject["dismissOnClickOutside"]?.asBoolean,
                )
            }
    }

    private lateinit var onSubmit: (String) -> Unit
    private lateinit var onCancel: () -> Unit
    fun bindCallback(onConfirm: (String) -> Unit, onCancel: () -> Unit): JsPromptConfiguration {
        this.onSubmit = onConfirm
        this.onCancel = onCancel
        return this
    }

    fun bindCallback(result: JsPromptResult): JsPromptConfiguration {
        bindCallback({ result.confirm(it) }, { result.cancel() })
        return this
    }

    @Composable
    fun openPromptDialog(requestDismiss: () -> Unit) {
        val config = this
        var resultText by remember {
            mutableStateOf(this.defaultValue)
        }
        val submitPrompt = {
            this.onSubmit(resultText)
            requestDismiss()
        }
        val cancelPrompt = {
            this.onCancel()
            requestDismiss()
        }
        AlertDialog(
            icon = { Icon(Icons.Filled.Edit, contentDescription = "Prompt") },
            onDismissRequest = cancelPrompt,
            title = { Text(text = this.title) },
            text = {
                TextField(
                    value = resultText,
                    singleLine = true,
                    onValueChange = {
                        resultText = it
                    }, label = { Text(text = this.label) }
                )
            },
            confirmButton = {
                TextButton(onClick = submitPrompt) { Text(config.confirmText) }
            },
            dismissButton = {
                TextButton(onClick = cancelPrompt) { Text(config.cancelText) }
            },
            properties = DialogProperties(
                dismissOnBackPress = this.dismissOnBackPress,
                dismissOnClickOutside = this.dismissOnClickOutside
            ),
        )
    }
}

data class JsConfirmConfiguration(
    val title: String,
    val message: String,
    val confirmText: String,
    val cancelText: String,
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = false,
) {
    companion object {
        operator fun invoke(
            title: String,
            label: String,
            confirmText: String,
            cancelText: String,
            dismissOnBackPress: Boolean? = null,
            dismissOnClickOutside: Boolean? = null,
        ) = JsConfirmConfiguration(
            title,
            label,
            confirmText,
            cancelText,
            dismissOnBackPress ?: true,
            dismissOnClickOutside ?: false,
        )

        val _gson =
            JsUtil.registerGsonDeserializer(JsConfirmConfiguration::class.java) { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                JsConfirmConfiguration(
                    jsonObject["title"]?.asString ?: "",
                    jsonObject["content"]?.asString ?: "",
                    jsonObject["confirmText"]?.asString ?: "",
                    jsonObject["cancelText"]?.asString ?: "",
                    jsonObject["dismissOnBackPress"]?.asBoolean,
                    jsonObject["dismissOnClickOutside"]?.asBoolean,
                )
            }
    }


    private lateinit var onConfirm: () -> Unit
    private lateinit var onCancel: () -> Unit
    fun bindCallback(onConfirm: () -> Unit, onCancel: () -> Unit): JsConfirmConfiguration {
        this.onConfirm = onConfirm
        this.onCancel = onCancel
        return this
    }

    fun bindCallback(result: JsResult): JsConfirmConfiguration {
        bindCallback({ result.confirm() }, { result.cancel() })
        return this
    }

    @Composable
    fun openConfirmDialog(requestDismiss: () -> Unit) {
        val config = this
        val submitConfirm = {
            this.onConfirm()
            requestDismiss()
        }
        val cancelConfirm = {
            this.onCancel()
            requestDismiss()
        }
        AlertDialog(
            icon = { Icon(Icons.Filled.QuestionMark, contentDescription = "Confirm") },
            onDismissRequest = cancelConfirm,
            title = { Text(text = config.title) },
            text = { Text(text = config.message) },
            confirmButton = {
                TextButton(onClick = submitConfirm) { Text(config.confirmText) }
            },
            dismissButton = {
                TextButton(onClick = cancelConfirm) { Text(config.cancelText) }
            },
            properties = DialogProperties(
                dismissOnBackPress = config.dismissOnBackPress,
                dismissOnClickOutside = config.dismissOnClickOutside
            ),
        )
    }

    @Composable
    fun openBeforeUnloadDialog(requestDismiss: () -> Unit) {
        val config = this
        val submitConfirm = {
            this.onConfirm()
            requestDismiss()
        }
        val cancelConfirm = {
            this.onCancel()
            requestDismiss()
        }
        AlertDialog(
            icon = { Icon(Icons.Filled.Warning, contentDescription = "Before Unload") },
            onDismissRequest = cancelConfirm,
            title = { Text(text = config.title) },
            text = { Text(text = config.message) },
            confirmButton = {
                TextButton(onClick = submitConfirm) { Text(config.confirmText) }
            },
            dismissButton = {
                TextButton(onClick = cancelConfirm) { Text(config.cancelText) }
            },
            properties = DialogProperties(
                dismissOnBackPress = config.dismissOnBackPress,
                dismissOnClickOutside = config.dismissOnClickOutside
            ),
        )
    }
}


class DialogFFI(
    val jsUtil: JsUtil,
    val alertConfig: MutableState<JsAlertConfiguration?>,
    val promptConfig: MutableState<JsPromptConfiguration?>,
    val confirmConfig: MutableState<JsConfirmConfiguration?>,
    val beforeUnloadConfig: MutableState<JsConfirmConfiguration?>,
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