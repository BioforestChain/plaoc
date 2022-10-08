package org.bfchain.rust.plaoc.webView.topbar

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
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

    fun topBarNavigationBack():Boolean {
        state.doBack()
      return true
    }

    fun getTopBarEnabled(): Boolean {
        return state.enabled.value
    }

    fun setTopBarEnabled(isEnabled: Boolean): Boolean {
        state.enabled.value = isEnabled
//        Log.i(TAG, "toggleEnabled:${state.enabled.value}")
        return state.enabled.value
    }

    fun getTopBarOverlay(): Float? {
        return state.overlay.value
    }
    /**设置透明度*/
    fun setTopBarOverlay(isOverlay: String): Float? {
//      Log.i(TAG, "toggleOverlay:${state.overlay.value},${isOverlay}")
      state.overlay.value = isOverlay.toFloat()
      return state.overlay.value
    }

    fun getTopBarTitle(): String {
        return state.title.value ?: ""
    }
    fun hasTopBarTitle(): Boolean {
        return state.title.value != null
    }

    fun setTopBarTitle(str: String) {
        state.title.value = str
    }
    fun getTopBarHeight(): Float {
        return state.height.value
    }

    fun getTopBarActions(): DataString<List<TopBarAction>> {
        return DataString_From(state.actions)//.map { action -> toDataString(action) }
    }

    fun setTopBarActions(actionListJson: DataString<List<TopBarAction>>) {
        state.actions.clear()
//      Log.i(TAG,"actionListJson:${actionListJson}")
        val actionList = actionListJson.toData<List<TopBarAction>>(object :
            TypeToken<List<TopBarAction>>() {}.type)
        actionList.toCollection(state.actions)
//      actionList.forEach{
//        Log.i(TAG,"哈哈：${it}")
//      }
    }

    fun getTopBarBackgroundColor(): Int {
        return state.backgroundColor.value.toArgb()
    }

    fun setTopBarBackgroundColor(color: ColorInt) {
        state.backgroundColor.value = Color(color)
    }
    fun getTopBarForegroundColor(): Int {
        return state.foregroundColor.value.toArgb()
    }
    fun setTopBarForegroundColor(color: ColorInt) {
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
