package org.bfchain.plaoc.dweb.js.systemUi

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val description: String?,
    val size: Float?,
) {

    enum class IconType() {
        NamedIcon(),
        AssetIcon(),
    }

    companion object {
        operator fun invoke(
            source: String,
            type: IconType? = null,
            description: String? = null,
            size: Float? = null,
        ) = DWebIcon(
            source,
            type ?: IconType.NamedIcon,
            description,
            size,
        )

        val _gson = JsUtil.registerGsonDeserializer(
            DWebIcon::class.java, JsonDeserializer { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                DWebIcon(
                    jsonObject["source"].asString,
                    jsonObject["type"]?.asString?.let { IconType.valueOf(it) },
                    jsonObject["description"]?.asString,
                    jsonObject["size"]?.asFloat,
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
fun DWebIcon(icon: DWebIcon, modifier: Modifier = Modifier) {
    val internal_modifier = modifier.let {
        if (icon.size != null) {
            it.size(icon.size.dp)
        } else {
            it
        }
    }
    when (icon.type) {
        DWebIcon.IconType.NamedIcon -> IconByName(
            name = icon.source,
            contentDescription = icon.description,
            modifier = internal_modifier
        )
        DWebIcon.IconType.AssetIcon -> {
            val painter = rememberAsyncImagePainter(icon.source, imageLoader = _getSvgLoader())
            Icon(
                painter = painter,
                contentDescription = icon.description,
                tint = Color.Unspecified,
                modifier = internal_modifier
            )
        }
    }
}