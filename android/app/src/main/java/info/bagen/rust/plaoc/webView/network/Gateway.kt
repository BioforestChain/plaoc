package info.bagen.rust.plaoc.webView.network

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.fasterxml.jackson.core.JsonParser
import com.github.yitter.idgen.YitIdHelper
import info.bagen.rust.plaoc.*
import info.bagen.libappmgr.utils.JsonUtil
import info.bagen.rust.plaoc.webView.urlscheme.CustomUrlScheme
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.text.toByteArray

private const val TAG = "Gateway"

fun ByteArray.toHexString(): String {
  return this.joinToString(",") {
    java.lang.String.format("0x%02X", it)
  }
}

/**
 * 传递dwebView到deno的消息,单独对转发给deno-js 的请求进行处理
 * https://channelId.bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/poll
 */
fun messageGateWay(
  stringData: String
) {
  Log.i(TAG, " messageGateWay: $stringData")
  denoService.backDataToRust(stringData.toByteArray())// 通知
//  println("messageGeWay back_data-> $backData")
}

/** 转发给ui*/
fun uiGateWay(
  stringData: String
): String {
  Log.i(TAG, " uiGateWay2: $stringData")
  if (stringData.isEmpty()) return ""
  try {
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) // 允许使用单引号包裹字符串
    val handle = mapper.readValue(stringData, JsHandle::class.java)
    val funName = ExportNativeUi.valueOf(handle.function);
    // 执行函数
    val result = call_ui_map[funName]?.let { it ->
      it(handle.data)
    }
    println("uiGateWayFunction:$funName = $result")
    createBytesFactory(ExportNative.SetDWebViewUI, result.toString())
    return result.toString()
  } catch (e:Exception) {
    e.message?.let { Log.e("uiGateWay：", it) }
  }
  return ""
}


// 视图文件拦截
fun viewGateWay(
  customUrlScheme: CustomUrlScheme,
  request: WebResourceRequest
): WebResourceResponse {
  var url = request.url.toString().lowercase(Locale.ROOT)
  if (url.contains("?")) {
    url = url.split("?", limit = 2)[0];
  }
//  Log.i(TAG, " viewGateWay: $url,contains: ${front_to_rear_map.contains(url)}")
  if (front_to_rear_map.contains(url)) {
    val trueUrl = front_to_rear_map[url]
    Log.i(TAG, " viewGateWay: $trueUrl")
    if (trueUrl != null) {
      // 远程文件处理
      if (trueUrl.startsWith("https") || trueUrl.startsWith("http")) {
        val connection = URL(trueUrl).openConnection() as HttpURLConnection
        connection.requestMethod = request.method
        return WebResourceResponse(
          "text/html",
          "utf-8",
          connection.inputStream
        )
      }
      // 本地文件处理
      return customUrlScheme.handleRequest(request, trueUrl)
    }
  }
  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream(
      JsonUtil.toJson(
        Response(false, "无权限，需要前往后端配置")
      ).toByteArray()
    )
  )
}


/** 注册channelId*/
fun registerChannelId(): WebResourceResponse {
  val newId = YitIdHelper.nextId()
//  println("TightUUID: ${newId}")
  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream(newId.toString().toByteArray())
  )
}
