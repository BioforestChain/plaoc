package org.bfchain.rust.plaoc.system

import android.content.res.AssetManager
import android.util.Log
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.rust.plaoc.*
import org.bfchain.rust.plaoc.system.file.*
import org.bfchain.rust.plaoc.webView.network.initMetaData
import org.bfchain.rust.plaoc.webView.sendToJavaScript


private val fileSystem = FileSystem()

/** 初始化系统后端app*/
fun initServiceApp(assets: AssetManager) {
  val serviceId = arrayListOf("HE74YAAL")
  serviceId.forEach { id ->
   try {
     val dApp  = FilesUtil.getDAppInfo(id)
     dApp?.manifest?.bfsaEntry?.let {
       createWorker(WorkerNative.valueOf("ReadOnlyRuntime"), RORuntime(it,assets).toString())
     }
   } catch (e:Exception) {
     Log.i("initServiceApp: ", e.toString())
   }
  }
}

data class RORuntime(
  val url: String = "",
  val assets: AssetManager = App.appContext.assets
)

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
  callable_map[ExportNative.ReadOnlyRuntime] = {
    val handle = mapper.readValue(it, RORuntime::class.java)
    denoService.onlyReadRuntime(handle.assets,handle.url)
  }
  callable_map[ExportNative.EvalJsRuntime] =
    { sendToJavaScript(it) }
  /** fs System*/
  callable_map[ExportNative.FileSystemLs] = {
//    Log.i("FileSystemLs:",it)
    val handle = mapper.readValue(it, FileLs::class.java)
    fileSystem.ls(handle.path,handle.option.filter, handle.option.recursive)
  }
  callable_map[ExportNative.FileSystemList] = {
//    Log.i("FileSystemList:",it)
    val handle = mapper.readValue(it, FileLs::class.java)
    fileSystem.list(handle.path)
  }
  callable_map[ExportNative.FileSystemMkdir] = {
    val handle = mapper.readValue(it, FileLs::class.java)
    fileSystem.mkdir(handle.path,handle.option.recursive)
  }
  callable_map[ExportNative.FileSystemWrite] = {
    val handle = mapper.readValue(it, FileWrite::class.java)
    fileSystem.write(handle.path,handle.option.content, handle.option.append,handle.option.autoCreate)
  }
  callable_map[ExportNative.FileSystemRead] = {
    val handle = mapper.readValue(it, FileRead::class.java)
    fileSystem.read(handle.path)
  }
  callable_map[ExportNative.FileSystemRm] = {
    val handle = mapper.readValue(it, FileRm::class.java)
    fileSystem.rm(handle.path,handle.option.deepDelete)
  }

}

