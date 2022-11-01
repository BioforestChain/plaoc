package org.bfchain.rust.plaoc.webView.network

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.fasterxml.jackson.databind.DeserializationFeature
import org.bfchain.libappmgr.utils.JsonUtil
import org.bfchain.rust.plaoc.*
import org.bfchain.rust.plaoc.webView.urlscheme.CustomUrlScheme
import java.io.ByteArrayInputStream
import java.net.URL
import java.nio.ByteBuffer
import java.util.*


private const val TAG = "NetworkMap"

// 这里是存储客户端的映射规则的，这样才知道需要如何转发给后端 <String,ImportMap>
val front_to_rear_map = mutableMapOf<String, String>()
var dWebView_host = ""

// ð这里是路由的白名单
var network_whitelist = "http://127.0.0.1"

/** 分发解析的请求*/
fun resolveNetworkRequest(
  request: WebResourceRequest,
  path: String,
  header: String,
  channelId: String,
)
  : WebResourceResponse {
  // 拿到消息体，转换为结构体
  val stringHex = path.substring(path.lastIndexOf("=") + 1);

  //拦截转发到后端的事件
  if (path.startsWith("/poll")) {
    messageGateWay(stringHex)
  } else if (path.startsWith("/setUi")) { //拦截设置ui的请求，代替JavascriptInterface
    val stringData = String(hexStrToByteArray(stringHex))
    println("resolveNetworkRequest: $stringData")
    val result = uiGateWay(stringData)
    networkResponse(channelId, header, result)
  }

  val suffixIndex = path.lastIndexOf(".")
  // 只拦截数据文件,忽略资源文件
  if (suffixIndex == -1) {
    dataGateWay(request)
  }

  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream(JsonUtil.toJson(Response()).toByteArray())
  )
}

/** 抽离拦截请求*/
fun interceptNetworkRequests(
  request: WebResourceRequest,
  customUrlScheme: CustomUrlScheme
): WebResourceResponse {
  val url = request.url.toString()
  val path = request.url.path
  // 防止卡住请求为空而崩溃
  if (!url.isNullOrEmpty() && !path.isNullOrEmpty()) {
    val temp = url.substring(url.lastIndexOf("/") + 1)
    // 注册channelId
    if (path == "/channel/registry") {
      return registerChannelId()
    }
    // 解析接收数据
    if (temp.startsWith("chunk")) {
      // 拿到channelID
      val channelId = path.substring(path.lastIndexOf("/channel/") + 9, path.lastIndexOf("/chunk"))
      parseChunkBinary(request, channelId, temp)
    }

    // 跳过白名单
    if (jumpWhitelist(url)) {
      // 拦截视图文件
      if (path.endsWith(".html")) {
        return viewGateWay(customUrlScheme, request)
      }
      // 映射本地文件的资源文件 https://bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/index.mjs -> /plaoc/index.mjs
      if (Regex(dWebView_host.lowercase(Locale.ROOT)).containsMatchIn(url)) {
        return customUrlScheme.handleRequest(request, path)
      }
    }
  }

  return WebResourceResponse(
    "application/json",
    "utf-8",
    ByteArrayInputStream(JsonUtil.toJson(Response(false)).toByteArray())
  )
}

var header = ""
var arrayBody = ""
var stringPath = ""

/**解析chunk 的数据*/
fun parseChunkBinary(
  request: WebResourceRequest,
  channelId: String, path: String
) {
  if (path.lastIndexOf("=") == -1) {
    return
  }
  // 拿到chunk的请求体
  val strBits = path.substring(path.lastIndexOf("=") + 1);
  // 拿到头部
  val headerId = strBits.substring(0, strBits.indexOf(":")).toInt()
  // 主体内容
  val hexBody = strBits.substring(strBits.indexOf(":") + 1)
  println("hexBody===>:${String(hexStrToByteArray(hexBody))}")
  // headerId偶数为请求头
  if (headerId.rem(2) == 0) {
    val stringBody = String(hexStrToByteArray(hexBody));
    // 解析出真正的请求
    val stringArray = stringBody.split("|")
    val stringData = stringArray[0]
    header = stringArray[1]
    // 拿到真正的请求消息
    stringPath = stringData.substring(stringData.lastIndexOf("/"))
    println("parseChunkBinary headerId:$headerId,${stringPath.lastIndexOf("=")}")
    // 表示为get请求,携带了param参数
    if (stringPath.lastIndexOf("=") != -1) {
      // 分发请求
      resolveNetworkRequest(request, stringPath, header, channelId)
      return
    }
  } else {
    // 如果发送的是奇数，表示发送了body消息
    arrayBody += hexBody
    // 如果消息结束了
    if(String(hexStrToByteArray(hexBody))== "true") {
      // 分发body请求
      resolveNetworkRequest(request, "$stringPath=$arrayBody", header, channelId)
      arrayBody = ""
    }
    return
  }
}

data class Response(
  val success: Boolean = true,
  val message: String? = "无权限，需要前往后端配置"
)


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

// 跳过白名单（因为每次请求都会走这个方法，所以抛弃循环的方法，用contains进行模式匹配，保证了速度）
fun jumpWhitelist(url: String): Boolean {
  val currentUrl = URL(url)
  if (network_whitelist.contains(currentUrl.host)) {
    return false
  }
  return true
}

/** 返回应用的虚拟路径 "https://$dWebView_host.dweb$path"*/
fun resolveUrl(path: String): String {
  return "https://${dWebView_host.lowercase(Locale.ROOT)}.dweb${shakeUrl(path)}"
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

