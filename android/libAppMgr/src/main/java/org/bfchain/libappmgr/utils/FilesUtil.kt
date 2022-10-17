package org.bfchain.libappmgr.utils

import android.os.Build
import org.bfchain.libappmgr.entity.AppInfo
import org.bfchain.libappmgr.entity.DAppInfo
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 主要用于文件的存储和读取操作，包括文件的解压操作
 */
object FilesUtil {
  const val TAG: String = "FilesUtil"
  val simpleDateFormat = SimpleDateFormat("yyyyMMddhhmmss")

  private val DIR_BOOT: String = "boot" // 存放 link.json 数据
  private val DIR_SYS: String = "sys" // system-app/bfs-id-xxx/sys 是运行程序路径
  private val DIR_AUTO_UPDATE: String = "tmp" + File.separator + "autoUpdate" // 存放最新版本的路径
  private val FILE_LINK_JSON: String = "link.json"
  private val FILE_BFSA_META_JSON: String = "bfsa-metadata.json"

  enum class APP_DIR_TYPE(val rootName: String) {
    // 内置应用
    RecommendApp(rootName = "recommend-app"),

    // 下载应用
    SystemApp(rootName = "system-app"),

    // 客户应用
    UserApp(rootName = "user-app"),
  }

  /**
   * 获取app的根目录
   */
  private fun getAndroidRootDirectory(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      AppContextUtil.sInstance!!.dataDir.absolutePath
    } else {
      AppContextUtil.sInstance!!.filesDir.absolutePath
    }
  }

  /**
   * 获取应用的根路径
   */
  private fun getAppRootDirectory(appType: APP_DIR_TYPE): String {
    return getAndroidRootDirectory() + File.separator + appType.rootName
  }

  /**
   * 获取应用的缓存路径
   */
  fun getAppCacheDirectory(): String {
    return AppContextUtil.sInstance!!.cacheDir.absolutePath
  }

  /**
   * 获取应用的缓存路径
   */
  fun getAppDownloadPath(): String {
    return getAppCacheDirectory() + File.separator + simpleDateFormat.format(Date()) + ".bfsa"
  }

  /**
   * 获取应用的解压路径
   */
  fun getAppUnzipPath(appInfo: AppInfo): String {
    return getAppRootDirectory(APP_DIR_TYPE.SystemApp) + File.separator //+ appInfo.bfsAppId
  }

  /**
   * 获取程序运行路径
   */
  fun getAppLauncherPath(appInfo: AppInfo): String {
    return getAppLauncherPath(appInfo.bfsAppId)
  }

  /**
   * 获取程序运行路径
   */
  fun getAppLauncherPath(appName: String): String {
    return getAppRootDirectory(APP_DIR_TYPE.SystemApp) + File.separator + appName +
      File.separator + DIR_SYS
  }

  /**
   * 获取程序运行路径
   */
  fun getAppDenoUrl(appId: String, entry: String): String {
    return getAppRootDirectory(APP_DIR_TYPE.SystemApp) + File.separator + appId +
      File.separator + entry
  }

  /**
   * 获取应用更新路径
   */
  fun getAppVersionSaveFile(appInfo: AppInfo): String {
    return getAppUpdateDirectory(appInfo) + File.separator + simpleDateFormat.format(Date()) + ".json"
  }

  /**
   * 获取应用更新路径
   */
  private fun getAppUpdateDirectory(appInfo: AppInfo): String {
    return getAppRootDirectory(getAppType(appInfo)) + File.separator + appInfo.bfsAppId +
      File.separator + DIR_AUTO_UPDATE
  }

  private fun getAppType(appInfo: AppInfo): APP_DIR_TYPE {
    return if (appInfo.isSystemApp) {
      APP_DIR_TYPE.SystemApp
    } else {
      APP_DIR_TYPE.RecommendApp
    }
  }

  /**
   * 获取应用更新路径中最新文件
   */
  fun getLastUpdateContent(appInfo: AppInfo): String? {
    var directory = getAppUpdateDirectory(appInfo)
    var file = File(directory)
    if (file.exists()) {
      var files = file.listFiles()
      return if (files.isNotEmpty()) {
        getFileContent(files.last().absolutePath)
      } else {
        null
      }
    }
    return null
  }

  /**
   * 获取当前目录子目录列表
   */
  fun getChildrenDirectoryList(rootPath: String): Map<String, String>? {
    // Log.d(TAG, "getChildrenDirectoryList $rootPath")
    return getChildrenDirectoryList(File(rootPath))
  }

  /**
   * 获取当前目录子目录列表
   */
  fun getChildrenDirectoryList(file: File): Map<String, String>? {
    if (file.exists()) {
      /*if (!file.exists()) {
        file.mkdirs()
      }*/
      var childrenMap: HashMap<String, String> = HashMap<String, String>()
      file.listFiles()?.forEach {
        if (it.isDirectory) {
          // Log.d(TAG, "name=${it.name}, absolutePath=${it.absolutePath}")
          childrenMap[it.name] = it.absolutePath
        }
      }
      if (childrenMap.isNotEmpty()) {
        return childrenMap
      }
    }
    return null
  }

  /**
   * 获取相应应用的图标, link.json中的icon路径进行修改，直接默认要求默认在sys目录
   */
  fun getAppIconPathName(appName: String, iconName: String, type: APP_DIR_TYPE): String {
    return getAppRootDirectory(type) + File.separator + appName + File.separator +
      DIR_SYS + File.separator + iconName
  }

  /**
   * 遍历当前目录及其子目录所有文件和文件夹
   */
  private fun traverseFileTree(fileName: String): List<String> {
    return traverseFileTree(File(fileName))
  }

  /**
   * 遍历当前目录及其子目录所有文件和文件夹
   */
  private fun traverseFileTree(file: File): List<String> {
    if (!file.exists()) {
      file.mkdirs()
    }
    var fileList = arrayListOf<String>()
    val fileTreeWalk = file.walk() // 遍历目录及其子目录所有文件和目录
    fileTreeWalk.iterator().forEach {
      fileList.add(it.absolutePath)
    }
    return fileList
  }

  /**
   * 获取文件全部内容字符串
   * @param filename
   */
  fun getFileContent(filename: String): String? {
    // Log.d("FilesUtil", "getFileContent filename->$filename")
    var file = File(filename)
    if (!file.exists()) {
      return null
    }
    return file.bufferedReader().use { it.readText() }
  }

  /**
   * 将content信息写入到文件中
   */
  fun writeFileContent(filename: String, content: String) {
    var file = File(filename)
    if (!file.parentFile.exists()) {
      file.parentFile.mkdirs()
    }
    if (!file.exists()) {
      file.createNewFile()
    }
    file.bufferedWriter().use { out -> out.write(content) }
  }

  /**
   * 末尾追加
   */
  private fun appendFileContent(filename: String, content: String) {
    var file: File = File(filename)
    if (!file.exists()) {
      file.createNewFile()
    }
    file.bufferedWriter().append(content)
  }

  fun deleteQuietly(file: File) {
    file.delete()
  }

  /**
   * 将assert目录下的文件拷贝到app目录remember-app目录下
   */
  fun copyAssetsToRecommendAppDir() {
    var rootPath = getAppRootDirectory(APP_DIR_TYPE.RecommendApp)
    var file = File(rootPath)
    file.delete() // 第一次运行程序时，recommend-app
    copyFilesFassets(
      APP_DIR_TYPE.RecommendApp.rootName,
      getAppRootDirectory(APP_DIR_TYPE.RecommendApp)
    )
  }

  /**
   *  从assets目录中复制整个文件夹内容
   *  @param  oldPath  String  原文件路径  如：/aa
   *  @param  newPath  String  复制后路径  如：xx:/bb/cc
   */
  private fun copyFilesFassets(oldPath: String, newPath: String) {
    try {
      var context = AppContextUtil.sInstance
      val fileNames = context!!.assets.list(oldPath) //获取assets目录下的所有文件及目录名，空目录不会存在
      if (fileNames != null) {
        if (fileNames.isNotEmpty()) { // 目录
          val file = File(newPath);
          file.mkdirs();//如果文件夹不存在，则递归
          fileNames.forEach {
            copyFilesFassets(
              oldPath + File.separator + it,
              newPath + File.separator + it
            )
          }
        } else {// 文件
          var inputStream = context!!.assets.open(oldPath)
          var outputStream = FileOutputStream(newPath)
          var read: Int = inputStream.read()
          while (read != -1) {
            outputStream.write(read)
            read = inputStream.read()
          }
          outputStream.flush()
          outputStream.close()
          inputStream.close()
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * 获取system-app和remember-app目录下的所有appinfo
   */
  fun getAppInfoList(): List<AppInfo> {
    // 1.从system-app/boot/bfs-app-id/boot/link.json取值，将获取到内容保存到appInfo中
    // 2.将system-app中到bfs-app-id信息保存到map里面，用于后续交验
    // 3.从remember-app/boot/bfs-app-id/boot/link.json取值，补充到列表中
    val systemAppMap =
      getChildrenDirectoryList(getAppRootDirectory(APP_DIR_TYPE.SystemApp))
    val recommendAppMap =
      getChildrenDirectoryList(getAppRootDirectory(APP_DIR_TYPE.RecommendApp))
    val appInfoList = ArrayList<AppInfo>()
    val systemAppExist = HashMap<String, String>()
    // Log.d(TAG, "$systemAppMap , $recommendAppMap")
    systemAppMap?.forEach {
      val appInfo =
        getFileContent(
          it.value + File.separator + DIR_BOOT + File.separator + FILE_LINK_JSON
        )?.let { it1 ->
          JsonUtil.getAppInfoFromLinkJson(it1, APP_DIR_TYPE.SystemApp)
        }
      // Log.d(TAG, "getAppInfoList system-app $appInfo")
      if (appInfo != null) {
        appInfoList.add(appInfo)
        systemAppExist[it.key] = it.value
      }
    }
    recommendAppMap?.forEach {
      if (!systemAppExist.containsKey(it.key)) {
        val appInfo =
          getFileContent(
            it.value + File.separator + DIR_BOOT + File.separator + FILE_LINK_JSON
          )?.let { it1 ->
            JsonUtil.getAppInfoFromLinkJson(it1, APP_DIR_TYPE.RecommendApp)
          }
        // Log.d(TAG, "getAppInfoList recommend-app appInfo=$appInfo")
        if (appInfo != null) {
          appInfoList.add(appInfo)
        }
      }
    }
    return appInfoList
  }

  /**
   * 获取需要轮询的应用列表
   */
  fun getScheduleAppList(): List<AppInfo> {
    return getAppInfoList()
  }

  /**
   * 根据bfsAppId获取DAppInfo的版本信息
   */
  fun getDAppInfo(bfsAppId: String): DAppInfo? {
    val path = getAppRootDirectory(APP_DIR_TYPE.SystemApp) + File.separator + bfsAppId +
      File.separator + DIR_BOOT + File.separator + FILE_BFSA_META_JSON
    return JsonUtil.getDAppInfoFromBFSA(getFileContent(path))
  }
}

fun String.parseFilePath(): String {
  if (this.lowercase().startsWith("file://")) {
    // return this.replace("file://", "")
    return this.substring(7, this.length)
  } else if (!this.startsWith("./") && !this.startsWith("/")) {
    return "/$this"
  }
  return this
}
