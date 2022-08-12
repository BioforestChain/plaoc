package org.bfchain.rust.plaoc.webView.topbar

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import org.bfchain.rust.plaoc.webView.icon.DWebIcon
import org.bfchain.rust.plaoc.webView.jsutil.*


private const val TAG = "TopBarFFI"

class TopBarFFI(
    val state: TopBarState,
) {

    @JavascriptInterface
    fun back() {
        state.doBack()
    }

    @JavascriptInterface
    fun getEnabled(): Boolean {
        return state.enabled.value
    }

    @JavascriptInterface
    fun toggleEnabled(isEnabled: BoolInt): Boolean {
        state.enabled.value = isEnabled.toBoolean { !state.enabled.value }
        Log.i(TAG, "toggleEnabled:${state.enabled.value}")
        return state.enabled.value
    }

    @JavascriptInterface
    fun getOverlay(): Boolean {
        return state.overlay.value
    }

    @JavascriptInterface
    fun toggleOverlay(isOverlay: BoolInt): Boolean {
        state.overlay.value = isOverlay.toBoolean { !state.overlay.value }
        Log.i(TAG, "toggleOverlay:${state.overlay.value}")
        return state.overlay.value
    }

    @JavascriptInterface
    fun getTitle(): String {
        return state.title.value ?: ""
    }

    @JavascriptInterface
    fun hasTitle(): Boolean {
        return state.title.value != null
    }

    @JavascriptInterface
    fun setTitle(str: String) {
        state.title.value = str
    }


    @JavascriptInterface
    fun getHeight(): Float {
        return state.height.value
    }

    @JavascriptInterface
    fun getActions(): DataString<List<TopBarAction>> {
        return DataString_From(state.actions)//.map { action -> toDataString(action) }
    }

    @JavascriptInterface
    fun setActions(actionListJson: DataString<List<TopBarAction>>) {
        state.actions.clear()
        val actionList = actionListJson.toData<List<TopBarAction>>(object :
            TypeToken<List<TopBarAction>>() {}.type)
        actionList.toCollection(state.actions)
    }

    @JavascriptInterface
    fun getBackgroundColor(): Int {
        return state.backgroundColor.value.toArgb()
    }

    @JavascriptInterface
    fun setBackgroundColor(color: ColorInt) {
        state.backgroundColor.value = Color(color)
    }

    @JavascriptInterface
    fun getForegroundColor(): Int {
        return state.foregroundColor.value.toArgb()
    }

    @JavascriptInterface
    fun setForegroundColor(color: ColorInt) {
        state.foregroundColor.value = Color(color)
    }

    companion object {
        val _x = TopBarAction._gson
    }
}

data class TopBarAction(
    val icon: DWebIcon,
    val onClickCode: String,
    val disabled: Boolean,
) {

    companion object {
        operator fun invoke(
            icon: DWebIcon,
            onClickCode: String,
            disabled: Boolean? = null
        ) = TopBarAction(
            icon, onClickCode, disabled ?: false
        )

        val _gson = JsUtil.registerGsonDeserializer(
            TopBarAction::class.java, JsonDeserializer { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                TopBarAction(
                    context.deserialize(jsonObject["icon"], DWebIcon::class.java),
                    jsonObject["onClickCode"].asString,
                    jsonObject["disabled"]?.asBoolean,
                )
            }
        )
        private val _icon_gson = DWebIcon._gson
    }
}
