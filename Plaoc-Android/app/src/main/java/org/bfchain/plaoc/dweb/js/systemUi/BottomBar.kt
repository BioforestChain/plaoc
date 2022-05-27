package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
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
    private val height: MutableState<Float?>,
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
    fun toggleEnabled(isEnabledInt: BoolInt): Boolean {
        var isEnabled = isEnabledInt.toBooleanOrNull()
        if (isEnabled == null && enabled.value == null) {
            isEnabled = !this.isEnabled
        }
        enabled.value = isEnabled
        Log.i(TAG, "toggleEnabled:${isEnabled}")
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
        return height.value ?: 0F
    }

    @JavascriptInterface
    fun setHeight(heightDp: Float) {
        height.value = heightDp
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
    val colors: Colors?
) {

    data class Colors(
        val indicatorColor: ColorInt?,
        val iconColor: ColorInt?,
        val iconColorSelected: ColorInt?,
        val textColor: ColorInt?,
        val textColorSelected: ColorInt?
    ) {
        data class ColorState(override val value: Color) : State<Color> {}

        @Composable
        fun toNavigationBarItemColors(): NavigationBarItemColors {
            val defaultColors = NavigationBarItemDefaults.colors()
            val indicatorColor = indicatorColor?.toComposeColor() ?: defaultColors.indicatorColor;
            val iconColor =
                ColorState(iconColor?.toComposeColor() ?: defaultColors.iconColor(false).value)
            val iconColorSelected = ColorState(
                textColorSelected?.toComposeColor() ?: defaultColors.indicatorColor
            );
            val textColor =
                ColorState(textColor?.toComposeColor() ?: defaultColors.textColor(false).value)
            val textColorSelected = ColorState(
                textColorSelected?.toComposeColor() ?: defaultColors.indicatorColor
            );

            val colors = @Stable object : NavigationBarItemColors {
                override val indicatorColor: Color
                    @Composable get() = indicatorColor


                /**
                 * Represents the icon color for this item, depending on whether it is [selected].
                 *
                 * @param selected whether the item is selected
                 */
                @Composable
                override fun iconColor(selected: Boolean) = if (selected) {
                    iconColorSelected
                } else {
                    iconColor
                }

                /**
                 * Represents the text color for this item, depending on whether it is [selected].
                 *
                 * @param selected whether the item is selected
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
