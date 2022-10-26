package org.bfchain.rust.plaoc.webView.network

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.fasterxml.jackson.core.JsonParser
import com.github.yitter.idgen.YitIdHelper
import org.bfchain.rust.plaoc.DenoService
import org.bfchain.rust.plaoc.ExportNativeUi
import org.bfchain.rust.plaoc.JsHandle
import org.bfchain.rust.plaoc.mapper
import org.bfchain.rust.plaoc.webView.urlscheme.CustomUrlScheme
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


private const val TAG = "Gateway"

/**
 * 数据资源拦截
 */
fun dataGateWay(
  request: WebResourceRequest
): WebResourceResponse {
  val url = request.url.toString().lowercase(Locale.ROOT)
  Log.i(TAG, " dataGateWay: $url")
  if (front_to_rear_map.contains(url)) {
    val trueUrl = front_to_rear_map[url]
    Log.i(TAG, " dataGateWay front_to_rear_map.contains: $trueUrl")
    try {
      val connection = URL(trueUrl).openConnection() as HttpURLConnection
      connection.requestMethod = request.method
//        Log.i(TAG, " dataGateWay connection.inputStream: ${connection.inputStream}")
      return WebResourceResponse(
        "application/json",
        "utf-8",
        connection.inputStream
      )
    } catch (e: Exception) { // 处理用户在配置文件里写的资源或服务，但实际没有引发webview崩溃重载的情况
      return WebResourceResponse(
        "application/json",
        "utf-8",
        ByteArrayInputStream("This data service could not be found".toByteArray())
      )
    }
  }
  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream("No permission, need to go to the backend configuration".toByteArray())
  )
}

/**
 * 传递dwebView到deno的消息,单独对转发给deno-js 的请求进行处理
 * https://channelId.bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/poll
 */
fun messageGateWay(
  request: WebResourceRequest
): WebResourceResponse {
  val url = request.url.toString().lowercase(Locale.ROOT)
  Log.i(TAG, " messageGateWay: $url")
  val byteData = url.substring(url.lastIndexOf("=") + 1)
  DenoService().backDataToRust(hexStrToByteArray(byteData))// 通知
  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream("ok".toByteArray())
  )
}

/** 转发给ui*/
fun uiGateWay(
  request: WebResourceRequest
): WebResourceResponse {
  val url = request.url.toString().lowercase(Locale.ROOT)
  val byteData = url.substring(url.lastIndexOf("=") + 1)
  val stringData = String(hexStrToByteArray(byteData))
//  Log.i(TAG, " uiGateWay: $stringData")
  mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) // 允许使用单引号包裹字符串
  val handle = mapper.readValue(stringData, JsHandle::class.java)
  val funName = ExportNativeUi.valueOf(handle.function);
  // 执行函数
  val result = call_ui_map[funName]?.let { it ->
    it(handle.data)
  } ?: return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream("0".toByteArray())
  )
  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream(result.toString().toByteArray())
  )
}


// 视图文件拦截
fun viewGateWay(
  customUrlScheme: CustomUrlScheme,
  request: WebResourceRequest
): WebResourceResponse {
  val url = request.url.toString().lowercase(Locale.ROOT)
  Log.i(TAG, " viewGateWay: $url,contains: ${front_to_rear_map.contains(url)}")
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
    ByteArrayInputStream("无权限，需要前往后端配置".toByteArray())
  )
}


/** 注册channelId*/
fun registerChannelId(): WebResourceResponse {
  val newId = YitIdHelper.nextId()
  println("TightUUID: ${newId}")
  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream(newId.toString().toByteArray())
  )
}
