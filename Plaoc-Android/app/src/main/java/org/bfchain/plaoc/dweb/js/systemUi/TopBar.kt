package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import org.bfchain.plaoc.dweb.js.util.*


private const val TAG = "TopBarFFI"

class TopBarFFI(
    val onClickBackBotton: () -> Unit,
    val enabled: MutableState<Boolean>,
    val overlay: MutableState<Boolean>,
    val title: MutableState<String?>,
    val height: MutableState<Float>,
    val actions: SnapshotStateList<TopBarAction>,
    val backgroundColor: MutableState<Color>,
    val foregroundColor: MutableState<Color>,
) {

    @JavascriptInterface
    fun back() {
        onClickBackBotton()
    }

    @JavascriptInterface
    fun getEnabled(): Boolean {
        return enabled.value
    }

    @JavascriptInterface
    fun toggleEnabled(isEnabled: BoolInt): Boolean {
        enabled.value = isEnabled.toBoolean { !enabled.value }
        Log.i(TAG, "toggleEnabled:${enabled.value}")
        return enabled.value
    }

    @JavascriptInterface
    fun getOverlay(): Boolean {
        return overlay.value
    }

    @JavascriptInterface
    fun toggleOverlay(isOverlay: BoolInt): Boolean {
        overlay.value = isOverlay.toBoolean { !overlay.value }
        Log.i(TAG, "toggleOverlay:${overlay.value}")
        return overlay.value
    }

    @JavascriptInterface
    fun getTitle(): String {
        return title.value ?: ""
    }

    @JavascriptInterface
    fun hasTitle(): Boolean {
        return title.value != null
    }

    @JavascriptInterface
    fun setTitle(str: String) {
        title.value = str
    }


    @JavascriptInterface
    fun getHeight(): Float {
        return height.value
    }

    @JavascriptInterface
    fun getActions(): DataString<List<TopBarAction>> {
        return DataString_From(actions)//.map { action -> toDataString(action) }
    }

    @JavascriptInterface
    fun setActions(actionListJson: DataString<List<TopBarAction>>) {
        actions.clear()
        val actionList = actionListJson.toData<List<TopBarAction>>(object :
            TypeToken<List<TopBarAction>>() {}.type);
        actionList.toCollection(actions)
    }

    @JavascriptInterface
    fun getBackgroundColor(): Int {
        return backgroundColor.value.toArgb()
    }

    @JavascriptInterface
    fun setBackgroundColor(color: ColorInt) {
        backgroundColor.value = Color(color)
    }

    @JavascriptInterface
    fun getForegroundColor(): Int {
        return foregroundColor.value.toArgb()
    }

    @JavascriptInterface
    fun setForegroundColor(color: ColorInt) {
        foregroundColor.value = Color(color)
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
