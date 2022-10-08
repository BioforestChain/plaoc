package org.bfchain.rust.plaoc.system

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import org.bfchain.rust.plaoc.*
import org.bfchain.rust.plaoc.system.file.FileSystem
import org.bfchain.rust.plaoc.webView.network.initMetaData
import org.bfchain.rust.plaoc.webView.sendToJavaScript


private val fileSystem = FileSystem()

/** 初始化系统函数*/
 fun initSystemFn(activity: MainActivity) {
  callable_map[ExportNative.OpenQrScanner] = { activity.openScannerActivity() }
  callable_map[ExportNative.BarcodeScanner] = { activity.openBarCodeScannerActivity() }
  callable_map[ExportNative.OpenDWebView] = {
    activity.openDWebViewActivity(it)
  }
  callable_map[ExportNative.InitMetaData] = {
    initMetaData(it)
  }
  callable_map[ExportNative.DenoRuntime] = {
    denoService.denoRuntime(it)
  }
  callable_map[ExportNative.EvalJsRuntime] =
    { sendToJavaScript(it) }
  /** fs System*/
  callable_map[ExportNative.FileSystemLs] = {
//    val handle = mapper.readValue(it, object : TypeReference<SystemHandle<FileLs>>() {})
    val handle = mapper.readValue(it, FileLs::class.java)
    fileSystem.ls(handle.path,handle.filter, handle.recursive)
  }
  callable_map[ExportNative.FileSystemMkdir] = {
    val handle = mapper.readValue(it, FileLs::class.java)
    fileSystem.mkdir(handle.path,handle.recursive)
  }
  callable_map[ExportNative.FileSystemWrite] = {
    val handle = mapper.readValue(it, FileWrite::class.java)
    fileSystem.write(handle.path,handle.content, handle.append,handle.autoCreate)
  }
  callable_map[ExportNative.FileSystemRead] = {
    val handle = mapper.readValue(it, FileLs::class.java)
    fileSystem.read(handle.path)
  }
  callable_map[ExportNative.FileSystemRm] = {
    val handle = mapper.readValue(it, FileRm::class.java)
    fileSystem.rm(handle.path)
  }
}

data class FileLs (
  val path: String = "",
  val filter: String = "",
  val recursive: Boolean = false
  )

data class FileWrite (
  val path: String = "",
  val content: String = "",
  val append: Boolean = false,
  val autoCreate: Boolean = true
)
data class FileRm (
  val path: String = "",
  val deepDelete: Boolean = true
)
