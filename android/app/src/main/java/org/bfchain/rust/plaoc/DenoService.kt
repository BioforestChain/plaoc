package org.bfchain.rust.plaoc

import android.app.IntentService
import android.content.Intent
import android.content.res.AssetManager
import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.ByteBuffer


private const val TAG = "DENO_SERVICE"

/** 针对64位
 * 第一块分区：版本号（versionId） 2^8 8位，一个字节 1：表示消息，2：表示广播，3：心跳检测
 * 第二块分区：头部标记（headId） 2^16 16位 两个字节  根据版本号这里各有不同，假如是消息，就是0，1；如果是广播则是组
 * 第三块分区：数据主体 动态创建
 */
// 这里当做一个连接池，每当有客户端传过来方法就注册一下，返回的时候就知道数据是谁要的了 <handleFunction,headId>
val rust_call_map = mutableMapOf<String, ByteArray>()

// 存储版本号 <versionID,headerID>
val version_head_map = mutableMapOf<ByteArray, ByteArray>()
val mapper = ObjectMapper()

class DenoService : IntentService("DenoService") {

    companion object {
        init {
            // 加载rust编译的so
            System.loadLibrary("rust_lib")
        }
    }

    interface IHandleCallback {
        fun handleCallback(bytes: ByteArray)
    }

    interface IDenoCallback {
        fun denoCallback(bytes: ByteArray)
    }

    private external fun denoSetCallback(callback: IDenoCallback)
    private external fun nativeSetCallback(callback: IHandleCallback)
    private external fun initDeno(assets: AssetManager)
    external fun backDataToRust(
        bufferData: ByteArray,
    )

    external fun denoRuntime(assets: AssetManager, path: String)

    override fun onHandleIntent(p0: Intent?) {
        val appContext = applicationContext
        // rust 通知 kotlin doing sting
        nativeSetCallback(object : IHandleCallback {
            override fun handleCallback(bytes: ByteArray) {
                warpCallback(bytes)
            }
        })
        // 单项执行evalJs
        denoSetCallback(object : IDenoCallback {
            override fun denoCallback(bytes: ByteArray) {
                warpCallback(bytes, false) // 单工模式不要存储
            }
        })
        initDeno(appContext.assets) // BFS初始化的操作
    }
}

fun warpCallback(bytes: ByteArray, store: Boolean = true) {
    val (headId, stringData) = parseBytesFactory(bytes) // 处理二进制
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true) //允许出现特殊字符和转义符
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) //允许使用单引号
    val handle = mapper.readValue(stringData, RustHandle::class.java)
    val funName = (handle.function[0]).toString()
    if (store) {
        rust_call_map[funName] = headId     // 存一下头部标记，返回数据的时候才知道给谁,存储的调用的函数名跟头部标记一一对应
    }
    callable_map[funName]?.let { it -> it(handle.data) } // 执行函数
}

// 解析二进制数据
fun parseBytesFactory(bytes: ByteArray): ByteData {
    val versionId = bytes.sliceArray(0..0)
    val headId = bytes.sliceArray(1..2)
    val message = bytes.sliceArray(3 until bytes.size)
    version_head_map[headId] = versionId // 存版本号
    val stringData = String(message)
    Log.d("bytesFactory", "now versionId :${versionId}")
    Log.d("bytesFactory", "now headId:${headId}")
    Log.d("bytesFactory", "now message says:$stringData")
    return ByteData(headId, stringData)
}


/**
 * 创建二进制数据返回
 */
fun createBytesFactory(callFun: String, message: String): ByteArray {
    val headId = rust_call_map[callFun] ?: ByteArray(2).plus(0x00)
    val versionId = version_head_map[headId] ?: ByteArray(1).plus(0x01)
    val msgBit = message.encodeToByteArray()
    val result = ByteBuffer.allocate(headId.size + versionId.size + msgBit.size)
        .put(headId)
        .put(versionId)
        .put(msgBit)
    // 移除使用完的标记
    rust_call_map.remove(callFun)
    version_head_map.remove(headId)
    return result.array()
}


data class RustHandle(
    val function: Array<String> = arrayOf(""),
    val data: String = ""
)

data class jsHandle(
    val function: Array<String> = arrayOf(""),
    val data: String = "",
    val channelId: String = ""
)


data class ByteData(var headId: ByteArray, var stringData: String)
