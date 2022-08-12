package org.bfchain.rust.plaoc.webView.icon

import com.google.gson.JsonDeserializer
import org.bfchain.rust.plaoc.webView.jsutil.JsUtil


data class DWebIcon(
    val source: String,
    val type: IconType,
    val description: String?,
    val size: Float?,
) {

    enum class IconType {
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
