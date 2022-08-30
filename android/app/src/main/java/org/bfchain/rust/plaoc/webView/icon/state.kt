package org.bfchain.rust.plaoc.webView.icon

import com.google.gson.JsonDeserializer
import org.bfchain.rust.plaoc.webView.jsutil.JsUtil
import javax.inject.Inject


data class DWebIcon(
  var un_source:String?,
  var source: String,
  val type: IconType,
  val description: String?,
  val size: Float?,
) {

  var currentSource: String= ""

  enum class IconType {
        NamedIcon(),
        AssetIcon(),
    }

    companion object {
        operator fun invoke(
            un_source: String?,
            source: String,
            type: IconType? = null,
            description: String? = null,
            size: Float? = null,
        ) = DWebIcon(
          un_source,
            source,
            type ?: IconType.NamedIcon,
            description,
            size,
        )

        val _gson = JsUtil.registerGsonDeserializer(
            DWebIcon::class.java, JsonDeserializer { json, typeOfT, context ->
                val jsonObject = json.asJsonObject
                DWebIcon(
                    jsonObject["un_source"]?.asString,
                    jsonObject["source"].asString,
                    jsonObject["type"]?.asString?.let { IconType.valueOf(it) },
                    jsonObject["description"]?.asString,
                    jsonObject["size"]?.asFloat,
                )
            }
        )
    }
}
