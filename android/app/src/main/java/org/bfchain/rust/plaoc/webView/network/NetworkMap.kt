package org.bfchain.rust.plaoc.webView.network

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import org.bfchain.rust.plaoc.*
import org.bfchain.rust.plaoc.webView.urlscheme.CustomUrlScheme
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


private const val TAG = "NetworkMap"

// 这里是存储客户端的映射规则的，这样才知道需要如何转发给后端 <String,ImportMap>
val front_to_rear_map = mutableMapOf<String, String>()
var dWebView_host = ""

// ð这里是路由的白名单
var network_whitelist = "http://127.0.0.1"

/** 抽离拦截请求*/
fun interceptNetworkRequests(
  request: WebResourceRequest,
  customUrlScheme: CustomUrlScheme
): WebResourceResponse {
  val url = request.url.toString()
  // 防止卡住请求为空而崩溃
  if (!url.isNullOrEmpty()) {
    val temp = url.substring(url.lastIndexOf("/") + 1)
    //拦截转发到后端的事件
    if (temp.startsWith("poll")) {
      return messageGateWay(request)
    }
    //拦截设置ui的请求，代替JavascriptInterface
    if (temp.startsWith("setUi")) {
      return uiGateWay(request)
    }
    val suffixIndex = temp.lastIndexOf(".")
    // 只拦截数据文件,忽略资源文件
    if (suffixIndex == -1) {
      return dataGateWay(request)
    }
    // 跳过白名单
    if (jumpWhitelist(url)) {
      // 拦截视图文件
      if (url.endsWith(".html")) {
        return viewGateWay(customUrlScheme, request)
      }
      // 映射本地文件的资源文件 https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/index.mjs -> /plaoc/index.mjs
      if (Regex(dWebView_host).containsMatchIn(url)) {
        val path = URL(url).path
        return customUrlScheme.handleRequest(request, path)
      }
    }
  }
  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream("无权限，需要前往后端配置".toByteArray())
  )
}

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
  val handle = mapper.readValue(stringData, jsHandle::class.java)
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

/** 初始化app数据*/
fun initMetaData(metaData: String) {
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  val metaJson = mapper.readValue(metaData, UserMetaData::class.java)
  if (dWebView_host == "") return
  // 设置路由
  for (importMap in metaJson.dwebview.importmap) {
    front_to_rear_map[resolveUrl(importMap.url)] = importMap.response
  }
  // 默认入口全部加上加载路径，用户不用配置
  for (entry in metaJson.manifest.enters) {
    val main = shakeUrl(entry)
    Log.d(TAG, "initMetaData:entry=> ${resolveUrl(entry)} ,main=> $main")
    front_to_rear_map[resolveUrl(entry)] = main
  }
  // 设置白名单
  for (whitelist in metaJson.whitelist) {
    network_whitelist += whitelist
  }
  Log.d(TAG, "this is metaData:${network_whitelist}")
}

/** 返回应用的虚拟路径 "https://$dWebView_host.dweb$path"*/
fun resolveUrl(path: String): String {
  return "https://$dWebView_host.dweb${shakeUrl(path)}"
}

/** 适配路径没有 / 的尴尬情况，没有的话会帮你加上*/
fun shakeUrl(path: String): String {
  val pathname = if (path.startsWith("/")) {
    path
  } else {
    "/$path"
  }
  return pathname
}

// 跳过白名单（因为每次请求都会走这个方法，所以抛弃循环的方法，用contains进行模式匹配，保证了速度）
fun jumpWhitelist(url: String): Boolean {
  val currentUrl = URL(url)
  if (network_whitelist.contains(currentUrl.host)) {
    return false
  }
  return true
}

/**
 * 十六进制String转Byte数组
 *
 * @param str
 * @return
 */
fun hexStrToByteArray(str: String): ByteArray {
  if (str.isEmpty()) {
    return ByteArray(0)
  }
  val currentStr = str.split(",")
  val byteArray = ByteArray(currentStr.size)
  for (i in byteArray.indices) {
    byteArray[i] = currentStr[i].toInt().toByte()
  }
  return byteArray
}

// 读取到了配置文件 ， mock : https://62b94efd41bf319d22797acd.mockapi.io/bfchain/v1/getBlockInfo
/**
 * 1. 用户如果知道自己请求的是哪个dweb，那么用户在请求的时候会自己加上，域名前缀。如果在自己的DwebView里发送请求则不用携带前缀，只需要写请求路径。
 * 2. 在读取用户配置的时候，需要把前缀和请求的路径拼接起来。
 * 3. 这里的匹配需要使用正则匹配，用户如果填写了一个主域名，那么默认主域名下的所有资源都是被包括的。
 * 4. 存储的规则统一用小写的,因为kotlin拦截出来是小写的
 * (ps:前缀：https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb，请求的路径：getBlockInfo)
 */

//data class RearRouter(val url: String, val header: UserHeader)

data class UserMetaData(
  val baseUrl: String = "",
  val manifest: Manifest = Manifest("", arrayOf("xx"), "", arrayOf(""), "", arrayOf("")),
  val dwebview: ImportMap = ImportMap(arrayOf(DwebViewMap("", ""))),
  val whitelist: Array<String> = arrayOf("http://localhost")
)

data class Manifest(
// 应用所属链的名称（系统应用的链名为通配符“*”，其合法性由节点程序自身决定，不跟随链上数据）
  val origin: String = "",
// 开发者
  val author: Array<String> = arrayOf("BFChain"),
// 应用搜索的描述
  val description: String = "test Application",
// 应用搜索的关键字
  val keywords: Array<String> = arrayOf("new test", "App"),
// 应用ID，参考共识标准
//  val dwebId: String = "",
// 私钥文件，用于最终的应用签名
  val privateKey: String = "",
// 应用入口，可以配置多个，其中index为缺省名称。
// 外部可以使用 DWEB_ID.bfchain (等价同于index.DWEB_ID.bfchain)、admin.DWEB_ID.bfchain 来启动其它页面
  val enters: Array<String> = arrayOf("index.html")
)

data class ImportMap(
  val importmap: Array<DwebViewMap> = arrayOf(DwebViewMap("", ""))
)

data class DwebViewMap(
  val url: String = "",
  val response: String = ""
)


//fun test() {
//    val b =
//        RearRouter(
//            "https://62b94efd41bf319d22797acd.mockapi.io/bfchain/v1/getBlockInfo",
//            UserHeader("GET", "application/json", 200, "xxx hi  this is response")
//        )
//    val c1 =
//        RearRouter(
//            "hello_runtime.html",
//            UserHeader("GET", "application/json", 200, "xxx hi  this is response")
//        )
//    val c2 =
//        RearRouter(
//            "app/bfchain.dev/index.html",
//            UserHeader("GET", "application/json", 200, "xxx hi  this is response")
//        )
//    front_to_rear_map["https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/hello_runtime.html"] = c1
//    front_to_rear_map["https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/app/bfchain.dev/index.html"] =
//        c2
//    front_to_rear_map["https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/getblockinfo"] = b
//    front_to_rear_map["https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/getBlockHigh"] = b
//
//}

