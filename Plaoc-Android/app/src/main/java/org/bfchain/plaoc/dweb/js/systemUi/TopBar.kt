package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import org.bfchain.plaoc.dweb.js.util.*


private const val TAG = "TopBarFFI"

class TopBarFFI(
    val overlay: MutableState<Boolean>,
    val title: MutableState<String?>,
    val height: MutableState<Float>,
    val actions: SnapshotStateList<TopBarAction>,
) {
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

    companion object {
        val _x = TopBarAction._gson
    }
}

data class TopBarAction(
    val iconValue: String,
    val onClickCode: String,
    val iconType: IconType,
    val disabled: Boolean,
) {
    enum class IconType() {
        NamedIcon()
    }

    companion object {
        operator fun invoke(
            iconValue: String,
            onClickCode: String,
            iconType: IconType? = null,
            disabled: Boolean? = null
        ) = TopBarAction(
            iconValue, onClickCode, iconType ?: IconType.NamedIcon, disabled ?: false
        )

        val _gson = JsUtil.registerGsonDeserializer(
            TopBarAction::class.java, JsonDeserializer { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                TopBarAction(
                    jsonObject["iconValue"].asString,
                    jsonObject["onClickCode"].asString,
                    jsonObject["iconType"]?.asString?.let { IconType.valueOf(it) },
                    jsonObject["disabled"]?.asBoolean,
                )
            }
        )
    }
}
