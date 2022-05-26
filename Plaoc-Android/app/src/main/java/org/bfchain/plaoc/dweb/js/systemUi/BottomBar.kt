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


private const val TAG = "BottomBarFFI"

class BottomBarFFI(
    private val enabled: MutableState<Boolean?>,
    private val overlay: MutableState<Boolean>,
    private val height: MutableState<Float>,
    private val actions: SnapshotStateList<BottomBarAction>,
    private val backgroundColor: MutableState<Color>,
    private val foregroundColor: MutableState<Color>,
) {
    @JavascriptInterface
    fun getEnabled(): Boolean {
        return if (enabled.value == null) {
            actions.size > 0
        } else {
            enabled.value as Boolean
        }
    }

    val isEnabled: Boolean
        get() {
            return getEnabled()
        }


    @JavascriptInterface
    fun toggleEnabled(isEnabled: BoolInt): Boolean {
        val isEnabledBool = isEnabled.toBooleanOrNull()
        enabled.value = isEnabledBool
        Log.i(TAG, "toggleEnabled:${isEnabledBool}")
        return getEnabled()
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
    fun getHeight(): Float {
        return height.value
    }


    @JavascriptInterface
    fun getActions(): DataString<List<BottomBarAction>> {
        return DataString_From(actions)//.map { action -> toDataString(action) }
    }

    @JavascriptInterface
    fun setActions(actionListJson: DataString<List<BottomBarAction>>) {
        actions.clear()
        val actionList = actionListJson.toData<List<BottomBarAction>>(object :
            TypeToken<List<BottomBarAction>>() {}.type);
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
        private val _x = BottomBarAction._gson
    }
}


data class BottomBarAction(
    val icon: DWebIcon,
    val onClickCode: String,
    val label: String,
    val selected: Boolean,
    val selectable: Boolean,
    val disabled: Boolean,
) {

    companion object {
        operator fun invoke(
            icon: DWebIcon,
            onClickCode: String,
            label: String? = null,
            selected: Boolean? = null,
            selectable: Boolean? = null,
            disabled: Boolean? = null,
        ) = BottomBarAction(
            icon,
            onClickCode,
            label ?: "",
            selected ?: false,
            selectable ?: true,
            disabled ?: false,
        )

        val _gson = JsUtil.registerGsonDeserializer(
            BottomBarAction::class.java, JsonDeserializer { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                BottomBarAction(
                    context.deserialize(jsonObject["icon"], DWebIcon::class.java),
                    jsonObject["onClickCode"].asString,
                    jsonObject["label"]?.asString,
                    jsonObject["selected"]?.asBoolean,
                    jsonObject["selectable"]?.asBoolean,
                    jsonObject["disabled"]?.asBoolean,
                )
            }
        )
        private val _icon_gson = DWebIcon._gson
    }
}