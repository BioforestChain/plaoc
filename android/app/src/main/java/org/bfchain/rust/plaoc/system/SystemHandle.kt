package org.bfchain.rust.plaoc.system

import android.util.Log
import org.bfchain.libappmgr.utils.APP_DIR_TYPE
import org.bfchain.libappmgr.utils.FilesUtil
import org.bfchain.rust.plaoc.*
import org.bfchain.rust.plaoc.system.device.DeviceInfo
import org.bfchain.rust.plaoc.system.file.*
import org.bfchain.rust.plaoc.system.notification.MessageSource
import org.bfchain.rust.plaoc.system.notification.NotificationMsgItem
import org.bfchain.rust.plaoc.webView.network.initMetaData
import org.bfchain.rust.plaoc.webView.sendToJavaScript
import org.bfchain.rust.plaoc.system.notification.NotifyManager
import org.bfchain.rust.plaoc.system.permission.PermissionManager
import org.bfchain.rust.plaoc.system.permission.PermissionUtil.getActualPermissions
import org.bfchain.rust.plaoc.webView.network.dWebView_host


private val fileSystem = FileSystem()
private val notifyManager = NotifyManager()

/** 初始化系统后端app*/
fun initServiceApp() {
  val serviceId = arrayListOf("HE74YAAL")
  serviceId.forEach { id ->
   try {
     val dApp  = FilesUtil.getDAppInfo(id, APP_DIR_TYPE.AssetsApp)
     dApp?.manifest?.bfsaEntry?.let {
       val path = splicingPath(id,it)
       try {
         App.appContext.assets.open(path) // 判断文件是否存在
         createWorker(WorkerNative.valueOf("ReadOnlyRuntime"), path)
       } catch (e: java.io.FileNotFoundException) {
         Log.e("initServiceApp","not found ${e.message}")
         null
       }
     }
   } catch (e:Exception) {
     Log.i("initServiceApp: ", e.toString())
   }
  }
}

/** 拼接入口*/
fun splicingPath(bfsId:String, entry:String):String {
  if (entry.startsWith("./")) {
    return "$bfsId${entry.replace("./","/")}"
  }
  if (entry.startsWith("/")) {
    return  "$bfsId$entry"
  }
  return "$bfsId/$entry"
}

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
    println("ReadOnlyRuntime：$it")
    denoService.onlyReadRuntime(App.appContext.assets,it)
  }
  callable_map[ExportNative.EvalJsRuntime] =
    { sendToJavaScript(it) }
  /** fs System*/
  callable_map[ExportNative.FileSystemLs] = {
    val handle = mapper.readValue(it, FileLs::class.java)
    fileSystem.ls(handle.path,handle.option.filter, handle.option.recursive)
  }
  callable_map[ExportNative.FileSystemList] = {
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
  callable_map[ExportNative.FileSystemReadBuffer] = {
    val handle = mapper.readValue(it, FileRead::class.java)
    fileSystem.readBuffer(handle.path)
  }
  callable_map[ExportNative.FileSystemRename] = {
    val handle = mapper.readValue(it, FileRename::class.java)
    fileSystem.rename(handle.path,handle.newPath)
  }
  callable_map[ExportNative.FileSystemRm] = {
    val handle = mapper.readValue(it, FileRm::class.java)
    fileSystem.rm(handle.path,handle.option.deepDelete)
  }
  /**获取appId */
  callable_map[ExportNative.GetBfsAppId] = {
    createBytesFactory(ExportNative.GetBfsAppId, dWebView_host)
  }
   /**deviceInfo */
  callable_map[ExportNative.GetDeviceInfo] = {
    DeviceInfo().getDeviceInfo()
  }
  /**申请应用权限 */
  callable_map[ExportNative.ApplyPermissions] = {
    val per = getActualPermissions(it)
    println("ApplyPermissions:$per")
    PermissionManager(activity).requestPermissions(per)
  }
  /** Notification */
  callable_map[ExportNative.CreateNotificationMsg] = {
    val message = mapper.readValue(it, NotificationMsgItem::class.java)
   val channelType =  when(message.msg_src) {
     "app_message" -> NotifyManager.ChannelType.DEFAULT
     "push_message"-> NotifyManager.ChannelType.IMPORTANT
     else ->  NotifyManager.ChannelType.DEFAULT
   }
    notifyManager.createNotification(
      title = message.title,
      text = message.msg_content,
      bigText = message.msg_content,
      channelType = channelType,
    )
  }
}


