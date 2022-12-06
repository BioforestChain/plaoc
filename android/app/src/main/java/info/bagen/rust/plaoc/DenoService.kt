package info.bagen.rust.plaoc

import android.app.IntentService
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import info.bagen.rust.plaoc.system.deepLink.DWebReceiver
import info.bagen.rust.plaoc.util.PlaocUtil
import info.bagen.rust.plaoc.webView.network.toHexString
import java.nio.ByteBuffer
import kotlin.concurrent.thread

private const val TAG = "DENO_SERVICE"

/** 针对64位
 * 第一块分区：版本号（versionId） 2^8 8位，一个字节 1：表示消息，2：表示广播，3：心跳检测
 * 第二块分区：头部标记（headId） 2^16 16位 两个字节  根据版本号这里各有不同，假如是消息，就是0，1；如果是广播则是组
 * 第三块分区：数据主体 动态创建
 */
// 这里当做一个连接池，每当有客户端传过来方法就注册一下，返回的时候就知道数据是谁要的了 <handleFunction,headId>
val rust_call_map = mutableMapOf<ExportNative, ByteArray>()

val zero_copy_buff = mutableMapOf<String, ByteArray>()

// 存储版本号 <versionID,headerID>
val version_head_map = mutableMapOf<ByteArray, ByteArray>()
val mapper = ObjectMapper()

// deno服务
val denoService = DenoService()

class DenoService : IntentService("DenoService") {

  companion object {
    init {
      // 加载rust编译的so
      System.loadLibrary("rust_lib")
    }
  }

  /**
   * 注册广播，用于接收广播信息
   */
  private var dWebReceiver = DWebReceiver()
  override fun onCreate() {
    super.onCreate()
    var intentFilter = IntentFilter()
    intentFilter.addAction(DWebReceiver.ACTION_OPEN_DWEB)
    registerReceiver(dWebReceiver, intentFilter)
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(dWebReceiver)
  }

  interface IHandleCallback {
    fun handleCallback(bytes: ByteArray)
  }

  interface IDenoZeroCopyBufCallback {
    fun denoZeroCopyBufCallback(bytes: ByteArray)
  }

  interface IRustCallback {
    fun rustCallback(bytes: ByteArray)
  }

  internal inner class CommBinder(private val service: DenoService) : Binder() {
    fun getService(): DenoService {
      return service
    }
  }

  private var mBinder = CommBinder(this@DenoService)

  override fun onBind(intent: Intent?): IBinder? {
    return mBinder
  }

  private external fun denoSetCallback(callback: IDenoZeroCopyBufCallback)
  private external fun nativeSetCallback(callback: IHandleCallback)
  private external fun rustCallback(callback: IRustCallback)

  /** 只读模式走这里*/
  external fun onlyReadRuntime(assets: AssetManager, target: String)

  /** 传递dwebView到deno-js的消息*/
  external fun backDataToRust(byte_data: ByteArray) // op_rust_to_js_buffer

  /** 这里负责直接返回数据到deno-js*/
  external fun backSystemDataToRust(byte_data: ByteArray) // op_rust_to_js_system_buffer
  external fun denoRuntime(path: String)

  @RequiresApi(Build.VERSION_CODES.S)
  @Deprecated("Deprecated in Java")
  override fun onHandleIntent(p0: Intent?) {
    // rust 通知 kotlin doing sting
    nativeSetCallback(object : IHandleCallback {
      override fun handleCallback(bytes: ByteArray) {
        println("handleCallback bytes:${bytes[0]}")
        warpCallback(bytes)
      }
    })
    // 传递zeroCopyBuffer
    denoSetCallback(object : IDenoZeroCopyBufCallback {
      override fun denoZeroCopyBufCallback(bytes: ByteArray) {
        warpZeroCopyBuffCallback(bytes) // 传递zeroCopyBuffer
      }
    })
    // rust直接返回到能力
    rustCallback(object : IRustCallback {
      override fun rustCallback(bytes: ByteArray) {
        warpRustCallback(bytes)
      }
    })
  }
}

fun warpCallback(bytes: ByteArray) {
  val (versionId, headId, stringData) = parseBytesFactory(bytes) // 处理二进制
  mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true) //允许出现特殊字符和转义符
  mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) //允许使用单引号
  val handle = mapper.readValue(stringData, RustHandle::class.java)
  val funName = ExportNative.valueOf(handle.cmd)
  println("warpCallback 🤩headId:${headId[0]},${headId[1]},funName:$funName,type:${handle.type}")
  if (handle.type == TransformType.HAS_RETURN.type) { // 有返回的 2
    version_head_map[headId] = versionId // 存版本号
    rust_call_map[funName] = headId     // 存一下头部标记，返回数据的时候才知道给谁,存储的调用的函数名跟头部标记一一对应
  }
  println("transferable_metadata:${handle.transferable_metadata.isNotEmpty()},cmd:${handle.cmd}")
  // 填充deno-js 发送的bufferView
  if (handle.transferable_metadata.isNotEmpty()) {
   val bufferArray =  matchZeroCopyBuff(headId,handle.data,handle.transferable_metadata)
    bufferArray.forEach { data ->
      callable_map[funName]?.let { it -> it(data.toHexString()) } // 执行函数
    }
  } else {
    handle.data.forEach { data ->
      callable_map[funName]?.let { it -> it(data) } // 执行函数
    }
  }
}

/**
 * 填充来自deno-js的zeroCopyBuff
 */
fun warpZeroCopyBuffCallback(buffers:ByteArray){
  val req_id = buffers.sliceArray(0..1);
  val key = PlaocUtil.transformKey(req_id);
  var index = 0;
  while (zero_copy_buff["$key-$index"] == null) {
    index++
  }
  println("warpZeroCopyBuffCallback==》 req_id:[${key},${req_id[1]}],buffers:,${key}")
  zero_copy_buff["$key-$index"] = buffers;
}

fun warpRustCallback(bytes: ByteArray) {
  println("warpRustCallback==》 $bytes")
//  mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true) //允许出现特殊字符和转义符
//  mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) //允许使用单引号
//  val handle = mapper.readValue(String(bytes), RustHandle::class.java)
//  println("warpRustCallback==》 ${handle.data},${handle.function}")
}

// 解析二进制数据
fun parseBytesFactory(bytes: ByteArray): Triple<ByteArray, ByteArray, String> {
  val versionId = bytes.sliceArray(0..1)
  val headId = bytes.sliceArray(2..3)
  val message = bytes.sliceArray(4 until bytes.size)
  val stringData = String(message,Charsets.UTF_16LE);

  println("parseBytesFactory🍙 $stringData,headId:[${headId[0]},${headId[1]}] ")
  return Triple(versionId, headId, stringData)
}


/*** 创建二进制数据返回*/
fun createBytesFactory(callFun: ExportNative, message: String) {
  val headId = rust_call_map[callFun] ?: ByteArray(2).plus(0x01)
  val versionId = version_head_map[headId] ?: ByteArray(2).plus(0x01)
  val msgBit = message.encodeToByteArray()
  val result = ByteBuffer.allocate(headId.size + versionId.size + msgBit.size)
    .put(versionId)
    .put(headId)
    .put(msgBit)
  // 移除使用完的标记
  rust_call_map.remove(callFun)
  version_head_map.remove(headId)
  println("安卓返回数据:callFun:${callFun.type}---headViewId=> ${headId[0]},${headId[1]},message=> $message")
  thread {
    denoService.backSystemDataToRust(result.array())
  }
}

// 填充数据返回
fun matchZeroCopyBuff(headId:ByteArray,data:Array<String>,transferable_metadata: Array<Int>):Array<ByteArray>{
  val request:Array<ByteArray> = arrayOf();
  val key = PlaocUtil.transformKey(headId);
  data.map { index ->
    val buff = zero_copy_buff["$key-$index"]
    println("kotlin#matchZeroCopyBuff,key:$key-$index,buff:$buff")
    if (buff !== null) {
      request[transferable_metadata[index.toInt()]] = buff
    }
  }
  return request
}

enum class TransformType(val type: Number) {
   HAS_RETURN(2),
   COMMON(1),
}

data class RustHandle(
  val cmd: String = "",
  val type: Number = 0,
  val data:Array<String> = arrayOf(""),
  val transferable_metadata:Array<Int> = arrayOf()
)

data class JsHandle(
  val function: String = "",
  val data: String = "",
  val channelId: String? = ""
)
