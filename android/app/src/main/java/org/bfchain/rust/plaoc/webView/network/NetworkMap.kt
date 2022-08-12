package org.bfchain.rust.plaoc.webView.network

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
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


/**
 * 数据资源拦截
 *  单独对转发给deno-js 的请求进行处理
 *  https://channelId.bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/poll
 *  https://channelId.bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/done
 */
fun dataGateWay(
    request: WebResourceRequest
): WebResourceResponse {
    val url = request.url.toString().lowercase(Locale.ROOT)
    Log.i(TAG, " dataGateWay: $url")
    if (front_to_rear_map.contains(url)) {
        val trueUrl = front_to_rear_map[url]
        val connection = URL(trueUrl).openConnection() as HttpURLConnection
        connection.requestMethod = request.method
        Log.i(TAG, " dataGateWay front_to_rear_map.contains: $trueUrl")
        Log.i(TAG, " dataGateWay connection.inputStream: ${connection.inputStream}")
        return WebResourceResponse(
            "application/json",
            "utf-8",
            connection.inputStream
        )
    }
    return WebResourceResponse(
        "application/json",
        "utf-8",
        ByteArrayInputStream("access denied".toByteArray())
    )
}

// 传递dwebView到deno的消息
fun messageGateWay(
    request: WebResourceRequest
): WebResourceResponse {
    val url = request.url.toString().lowercase(Locale.ROOT)
    Log.i(TAG, " messageGateWay: $url")
    val byteData = url.substring(url.lastIndexOf("=") + 1)
    DenoService().backDataToRust(hexStrToByteArray(byteData))// 通知

//    val stringData = String(hexStrToByteArray(byteData))
//    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
//    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) //允许使用单引号
//    val handle = mapper.readValue(stringData, jsHandle::class.java)
//    val funName = (handle.function[0]).toString()
//    // 执行函数
//    callable_map[funName]?.let { it -> it(handle.data) }

    return WebResourceResponse(
        "application/json",
        "utf-8",
        ByteArrayInputStream("ok".toByteArray())
    )
}

// 视图文件拦截
fun viewGateWay(
    customUrlScheme: CustomUrlScheme,
    request: WebResourceRequest
): WebResourceResponse {
    val url = request.url.toString().lowercase(Locale.ROOT)
    Log.i(TAG, " viewGateWay: ${request.url}")
//    Log.i(TAG, " viewGateWay: ${front_to_rear_map.contains(url)}")
    if (front_to_rear_map.contains(url)) {
        val trueUrl = front_to_rear_map[url]
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
        ByteArrayInputStream("access denied".toByteArray())
    )
}

fun initMetaData(metaData: String) {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val metaJson =
        mapper.readValue(metaData, UserMetaData::class.java)
    // 设置域名
    dWebView_host = metaJson.baseUrl.lowercase(Locale.ROOT)
    // 设置路由
    for (importMap in metaJson.dwebview.importmap) {
        front_to_rear_map[resolveUrl(importMap.url)] = importMap.response
    }
    // 设置白名单
    for (whitelist in metaJson.whitelist) {
        network_whitelist += whitelist
    }
    Log.d(TAG, "this is metaData:${network_whitelist}")
}

fun resolveUrl(path: String): String {
    val pathname = if (path.startsWith("/")) {
        path
    } else {
        "/$path"
    }
    return (dWebView_host + pathname)
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
        byteArray[i] = currentStr[i].toByte()
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
    val manifest: Manifest = Manifest("", arrayOf("xx"), "", arrayOf(""), "", "", ""),
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
    val dwebId: String = "",
// 私钥文件，用于最终的应用签名
    val privateKey: String = "",
// 应用入口，可以配置多个，其中index为缺省名称。
// 外部可以使用 DWEB_ID.bfchain (等价同于index.DWEB_ID.bfchain)、admin.DWEB_ID.bfchain 来启动其它页面
    val enter: String = "index.html"
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

