package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import org.bfchain.plaoc.dweb.js.util.*


private const val TAG = "BottomBarFFI"

class BottomBarFFI(
    val enabled: MutableState<Boolean>,
    val overlay: MutableState<Boolean>,
    val height: MutableState<Float>,
    val actions: SnapshotStateList<BottomBarAction>,
    val backgroundColor: MutableState<Color>,
    val foregroundColor: MutableState<Color>,
) {
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


    companion object {
        private val _x = BottomBarAction._gson
    }
}


data class BottomBarAction(
    val icon: DWebIcon,
    val onClickCode: String,
    val label: String,
    val disabled: Boolean,
) {

    companion object {
        operator fun invoke(
            icon: DWebIcon,
            onClickCode: String,
            label: String? = null,
            disabled: Boolean? = null,
        ) = BottomBarAction(
            icon, onClickCode, label ?: "", disabled ?: false,
        )

        val _gson = JsUtil.registerGsonDeserializer(
            BottomBarAction::class.java, JsonDeserializer { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                BottomBarAction(
                    context.deserialize(jsonObject["icon"], DWebIcon::class.java),
                    jsonObject["onClickCode"].asString,
                    jsonObject["label"]?.asString,
                    jsonObject["disabled"]?.asBoolean,
                )
            }
        )
        private val _icon_gson = DWebIcon._gson
    }
}