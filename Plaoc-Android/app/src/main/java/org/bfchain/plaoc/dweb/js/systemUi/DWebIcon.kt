package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.LocalImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import com.google.gson.JsonDeserializer
import org.bfchain.plaoc.dweb.IconByName
import org.bfchain.plaoc.dweb.js.util.JsUtil

data class DWebIcon(
    val source: String,
    val type: IconType,
    val description: String?
) {

    enum class IconType() {
        NamedIcon(),
        AssetIcon(),
    }

    companion object {
        operator fun invoke(
            source: String,
            type: IconType? = null,
            description: String? = null
        ) = DWebIcon(
            source,
            type ?: IconType.NamedIcon,
            description,
        )

        val _gson = JsUtil.registerGsonDeserializer(
            DWebIcon::class.java, JsonDeserializer { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                DWebIcon(
                    jsonObject["source"].asString,
                    jsonObject["type"]?.asString?.let { IconType.valueOf(it) },
                    jsonObject["description"]?.asString,
                )
            }
        )
    }
}

private var svgLoader: ImageLoader? = null


@Composable
private fun _getSvgLoader(): ImageLoader {
    if (svgLoader == null) {
        svgLoader = ImageLoader.Builder(LocalContext.current)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
    return svgLoader as ImageLoader
}

@Composable
fun DWebIcon(icon: DWebIcon) {
    when (icon.type) {
        DWebIcon.IconType.NamedIcon -> IconByName(
            name = icon.source,
            contentDescription = icon.description
        )
        DWebIcon.IconType.AssetIcon -> {
            val painter = rememberAsyncImagePainter(icon.source, imageLoader = _getSvgLoader())
            Icon(
                painter = painter,
                contentDescription = icon.description,
                tint = Color.Unspecified
            )
        }
    }
}