package info.bagen.rust.plaoc.system

import android.util.Log
import info.bagen.rust.plaoc.*
import info.bagen.rust.plaoc.system.file.*
import info.bagen.libappmgr.utils.APP_DIR_TYPE
import info.bagen.libappmgr.utils.ClipboardUtil
import info.bagen.libappmgr.utils.FilesUtil
import info.bagen.rust.plaoc.system.device.DeviceInfo
import info.bagen.rust.plaoc.system.notification.NotificationMsgItem
import info.bagen.rust.plaoc.system.notification.NotifyManager
import info.bagen.rust.plaoc.system.permission.PermissionManager
import info.bagen.rust.plaoc.webView.jsutil.sendToJavaScript
import info.bagen.rust.plaoc.webView.network.*

val callable_map = mutableMapOf<ExportNative, (data: String) -> Unit>()
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
  callable_map[ExportNative.ExitApp] = {
    println("kotlin#app退出🏄🏼‍")
    App.dwebViewActivity?.finish()
  }
  // 初始化用户配置
  callable_map[ExportNative.InitMetaData] = {
    initMetaData(it)
  }
  // 执行ui函数
  callable_map[ExportNative.SetDWebViewUI] = {
    uiGateWay(it)
  }
  callable_map[ExportNative.DenoRuntime] = {
    denoService.denoRuntime(it)
  }
  callable_map[ExportNative.ReadOnlyRuntime] = {
    println("ReadOnlyRuntime：$it")
    denoService.onlyReadRuntime(App.appContext.assets,it)
  }
  // 返回数据给DWebView-js
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
    fileSystem.write(handle.path,handle.content, handle.option)
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
  callable_map[ExportNative.FileSystemStat] = {
    val handle = mapper.readValue(it, FileStat::class.java)
    Log.i("kotlin#FileSystemStat: ", handle.toString())
    fileSystem.stat(handle.path)
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
    PermissionManager.requestPermissions(activity, it)
  }
  /** Notification */
  callable_map[ExportNative.CreateNotificationMsg] = {
    createBytesFactory(ExportNative.CreateNotificationMsg, it)
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

  /** Clipboard */
  callable_map[ExportNative.ReadClipboardContent] = {
    createBytesFactory(ExportNative.ReadClipboardContent, ClipboardUtil.readFromClipboard(App.appContext) ?: "")
  }
  callable_map[ExportNative.WriteClipboardContent] = {
    ClipboardUtil.writeToClipboard(App.appContext, it)
  }
}


