package org.bfchain.libappmgr.utils

import android.os.Build
import android.util.Log
import org.bfchain.libappmgr.model.AppInfo
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * 主要用于文件的存储和读取操作，包括文件的解压操作
 */
object FilesUtil {
    const val TAG: String = "FilesUtil"
    val simpleDateFormat = SimpleDateFormat("yyyyMMddhhmmss")

    private val RememberApp: String = "remember-app" // 内置应用
    private val SystemApp: String = "system-app" // 下载应用后
    private val DIR_BOOT: String = "boot" // 存放 link.json 数据
    private val DIR_SYS: String = "sys" // 存放 icon 信息等
    private val FILE_LINK_JSON: String = "link.json"

    enum class APP_DIR_TYPE { RememberApp, SystemApp }

    /**
     * 获取应用的根路径
     */
    fun getAppDirectory(type: APP_DIR_TYPE): String {
        var rootName = when (type) {
            APP_DIR_TYPE.RememberApp -> RememberApp
            APP_DIR_TYPE.SystemApp -> SystemApp
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            AppContextUtil.sInstance!!.dataDir.absolutePath + File.separator + rootName // 获取apk安装路径
        } else {
            AppContextUtil.sInstance!!.filesDir.absolutePath + File.separator + rootName // 获取apk安装目录下的files路径
        }
    }

    /**
     * 获取应用的缓存路径
     */
    fun getAppCacheDirectory(): String {
        return AppContextUtil.sInstance!!.cacheDir.absolutePath
    }

    /**
     * 获取应用更新路径
     */
    fun getAppUpdateDirectory(appName: String): String {
        return getAppDirectory(APP_DIR_TYPE.RememberApp) + File.separator + appName +
                File.separator + "tmp" + File.separator + "autoUpdate"
    }

    /**
     * 获取应用更新路径中最新文件
     */
    fun getLastUpdateFile(appName: String): File? {
        var directory = getAppUpdateDirectory(appName)
        var file = File(directory)
        if (file.exists()) {
            return file.listFiles().last()
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
            if (!file.exists()) {
                file.mkdirs()
            }
            var childrenMap: HashMap<String, String> = HashMap<String, String>()
            file.listFiles().forEach {
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
     * 获取相应应用的图标
     */
    fun getAppIconPathName(appName: String, iconName: String, type: APP_DIR_TYPE): String {
        return getAppDirectory(type) + File.separator + appName + iconName
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

    /**
     * 将assert目录下的文件拷贝到app目录remember-app目录下
     */
    fun copyAssetsToRememberAppDir() {
        var rootPath = getAppDirectory(APP_DIR_TYPE.RememberApp)
        var file = File(rootPath)
        file.deleteRecursively() // 第一次运行程序时，强制删除remember-app
        copyFilesFassets(RememberApp, getAppDirectory(APP_DIR_TYPE.RememberApp))
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
            getChildrenDirectoryList(getAppDirectory(APP_DIR_TYPE.SystemApp))
        val rememberAppMap =
            getChildrenDirectoryList(getAppDirectory(APP_DIR_TYPE.RememberApp))
        val appInfoList = ArrayList<AppInfo>()
        val systemAppExist = HashMap<String, String>()
        // Log.d(TAG, "$systemAppMap , $rememberAppMap")
        systemAppMap?.forEach {
            val appInfo =
                getFileContent(
                    it.value + File.separator +
                            DIR_BOOT + File.separator + FILE_LINK_JSON
                )?.let { it1 ->
                    JsonUtil.getAppInfoFromLinkJson(it1, APP_DIR_TYPE.SystemApp)
                }
            // Log.d(TAG, "getAppInfoList system-app $appInfo")
            if (appInfo != null) {
                appInfoList.add(appInfo)
                systemAppExist[it.key] = it.value
            }
        }
        rememberAppMap?.forEach {
            if (!systemAppExist.containsKey(it.key)) {
                val appInfo =
                    getFileContent(
                        it.value + File.separator +
                                DIR_BOOT + File.separator + FILE_LINK_JSON
                    )?.let { it1 ->
                        JsonUtil.getAppInfoFromLinkJson(it1, APP_DIR_TYPE.RememberApp)
                    }
                // Log.d(TAG, "getAppInfoList remember-app appInfo=$appInfo")
                if (appInfo != null) {
                    appInfoList.add(appInfo)
                }
            }
        }
        return appInfoList
    }

    fun UnzipFile(zipFile: String, desDirectory: String) {
        // Log.d(TAG, "UnzipFile zipFile->$zipFile, desDirectory->$desDirectory")
        var fileZip = ZipFile(zipFile)
        var entries = fileZip.entries()
        File(desDirectory).mkdirs()
        while (entries.hasMoreElements()) {
            var zipEntry = entries.nextElement() as ZipEntry
            if ("__MACOSX" == zipEntry.name) continue // 这个文件是多余的，不解压
            // Log.d(TAG, "UnzipFile name->${zipEntry.name}")
            var unzipFilePath = desDirectory + File.separator + zipEntry.name
            if (zipEntry.isDirectory) {
                File(unzipFilePath).mkdirs()
            } else {
                var file = File(unzipFilePath)
                file.parentFile.mkdirs() // 确保上级目录存在
                var bufferedOutputStream = BufferedOutputStream(FileOutputStream(unzipFilePath))
                val bytes = ByteArray(1024 * 8)
                var readLen: Int
                // Java 与 Kotlin的不同之处，需要特别关注。
                // while ((readLen = zipInputStream.read(bytes))!=-1){
                var inputStream = fileZip.getInputStream(zipEntry)
                while (inputStream.read(bytes).also { readLen = it } > 0) {
                    bufferedOutputStream.write(bytes, 0, readLen)
                }
                bufferedOutputStream.close()
                inputStream.close()
            }
        }
    }
}