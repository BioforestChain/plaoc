package org.bfchain.rust.plaoc.webView.bottombar


import android.R.color
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import org.bfchain.rust.plaoc.webView.icon.DWebIcon
import org.bfchain.rust.plaoc.webView.jsutil.*


private const val TAG = "BottomBarFFI"

class BottomBarFFI(
    private val state: BottomBarState,
) {
    @JavascriptInterface
    fun getEnabled() = state.isEnabled

   // 控制是否隐藏bottom bar, 此方法如果不传不会调用，一传肯定是true，也就是不显示bottom bar
    @JavascriptInterface
    fun toggleEnabled(isEnabledBool: Boolean): Boolean? {
        state.enabled.value = !isEnabledBool
        return getEnabled()
    }

    @JavascriptInterface
    fun getOverlay(): Float {
        return state.overlay.value?: 1F
    }
    // 控制是开启bottom bar 遮罩。
    @JavascriptInterface
    fun toggleOverlay(isOverlay: String): Float {
        state.overlay.value = isOverlay.toFloat()
        Log.i(TAG, "toggleOverlay:${state.overlay.value}")
        return state.overlay.value ?: 1F
    }

    @JavascriptInterface
    fun getHeight(): Float {
        return state.height.value ?: 0F
    }

    @JavascriptInterface
    fun setHeight(heightDp: String) {
        state.height.value = heightDp.toFloat()
    }


    @JavascriptInterface
    fun getActions(): DataString<List<BottomBarAction>> {
        return DataString_From(state.actions)//.map { action -> toDataString(action) }
    }

    @JavascriptInterface
    fun setActions(actionListJson: DataString<List<BottomBarAction>>) {
        state.actions.clear()
      Log.i(TAG, "actionListJson:${actionListJson}")
      val actionList = actionListJson.toData<List<BottomBarAction>>(object :
            TypeToken<List<BottomBarAction>>() {}.type)
        actionList.toCollection(state.actions)
      actionList.forEach{
        Log.i(TAG, "actionList:${it.colors}")
      }
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
    val colors: Colors?
) {

  data class Colors(
        val indicatorColor: ColorInt?,
        val iconColor: ColorInt?,
        val iconColorSelected: ColorInt?,
        val textColor: ColorInt?,
        val textColorSelected: ColorInt?
    ) {
        data class ColorState(override val value: Color) : State<Color>

        @Composable
        fun toNavigationBarItemColors(): NavigationBarItemColors {
            val defaultColors = NavigationBarItemDefaults.colors()
           // indicatorColor 无法控制透明度
            val indicatorColor = indicatorColor?.toComposeColor() ?: defaultColors.indicatorColor
            val iconColor =
                ColorState(iconColor?.toComposeColor()?: defaultColors.iconColor(false).value)
            val iconColorSelected = ColorState(
                textColorSelected?.toComposeColor() ?: defaultColors.indicatorColor
            )
            val textColor =
                ColorState(textColor?.toComposeColor() ?: defaultColors.textColor(false).value)
            val textColorSelected = ColorState(
                textColorSelected?.toComposeColor()  ?: defaultColors.indicatorColor
            )

            val colors = @Stable object : NavigationBarItemColors {
                override val indicatorColor: Color
                    @Composable get() = indicatorColor

                /**
                 * 表示该项的图标颜色，取决于它是否被[选中]。
                 *
                 * @param selected 项目是否被选中
                 */
                @Composable
                override fun iconColor(selected: Boolean) = if (selected) {
                    iconColorSelected
                } else {
                    iconColor
                }

                /**
                 * 表示该项的文本颜色，取决于它是否被[选中]。
                 *
                 * @param selected 项目是否被选中
                 */
                @Composable
                override fun textColor(selected: Boolean) = if (selected) {
                    textColorSelected
                } else {
                    textColor
                }
            }
            return colors
        }
    }

    companion object {
        operator fun invoke(
            icon: DWebIcon,
            onClickCode: String,
            label: String? = null,
            selected: Boolean? = null,
            selectable: Boolean? = null,
            disabled: Boolean? = null,
            colors: Colors? = null
        ) = BottomBarAction(
            icon,
            onClickCode,
            label ?: "",
            selected ?: false,
            selectable ?: true,
            disabled ?: false,
            colors,
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
                    jsonObject["colors"]?.let {
                        context.deserialize(jsonObject["colors"], Colors::class.java)
                    }
                )
            }
        )
        private val _icon_gson = DWebIcon._gson
    }
}
