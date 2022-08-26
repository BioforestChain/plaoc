package org.bfchain.libappmgr.utils

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File

/**
 * 主要用于文件的存储和读取操作，包括文件的解压操作
 */
object FilesUtil {
    private val RememberApp: String = "remember-app"
    private val SystemApp: String = "system-app"

    enum class APP_DIR_TYPE { RememberApp, SystemApp }

    /**
     * 获取应用的根路径
     */
    fun getAppDirectory(context: Context, type: APP_DIR_TYPE): String {
        var appName = when (type) {
            APP_DIR_TYPE.RememberApp -> RememberApp
            APP_DIR_TYPE.SystemApp -> SystemApp
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.dataDir.absolutePath + File.separator + appName // 获取apk安装路径
        } else {
            context.filesDir.absolutePath + File.separator + appName // 获取apk安装目录下的files路径
        }
    }

    /**
     * 遍历应用第一级目录信息
     */
    fun traverseAppDirectory(context: Context, type: APP_DIR_TYPE): List<String> {
        return traverseFileTree(getAppDirectory(context, type))
    }

    fun getAppIconPathName(context: Context, appName: String, iconName: String): String {
        return getAppDirectory(context, APP_DIR_TYPE.RememberApp) + File.separator + appName +
                File.separator + "sys" + File.separator + iconName
    }

    /**
     * 遍历当前目录下第一级所有目录信息
     */
    private fun traverseFileTree(fileName: String): List<String> {
        Log.d("lin.huang", "traverseFileTree fileName=" + fileName)
        return traverseFileTree(File(fileName))
    }

    /**
     * 遍历当前目录下第一级所有目录信息
     */
    private fun traverseFileTree(file: File): List<String> {
        Log.d("lin.huang", "traverseFileTree exists1=" + file.exists())
        if (!file.exists()) {
            file.mkdirs()
        }
        Log.d("lin.huang", "traverseFileTree exists2=" + file.exists())
        var fileList = arrayListOf<String>()
        file.listFiles().forEach {
            Log.d("lin.huang", "path->" + it.absolutePath)
            if (it.isDirectory) {
                fileList.add(it.absolutePath)
            }
        }
        /*val fileTreeWalk = file.walk() // 遍历目录及其子目录所有文件和目录
        fileTreeWalk.iterator().forEach {
            Log.d("lin.huang", "path->" + it.absolutePath)
            fileList.add(it.absolutePath)
        }*/
        return fileList
    }

    /**
     * 获取文件全部内容字符串
     * @param filename
     */
    fun getFileContent(filename: String): String? {
        var file: File = File(filename)
        if (!file.exists()) {
            return null
        }
        return file.bufferedReader().use { it.readText() }
    }

    /**
     * 将content信息写入到文件中
     */
    private fun writeFileContent(filename: String, content: String) {
        var file: File = File(filename)
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
    fun copyAssetsToRememberAppDir(context: Context) {
        copyFilesFassets(
            context,
            RememberApp,
            getAppDirectory(context, APP_DIR_TYPE.RememberApp)
        )
    }

    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    private fun copyFilesFassets(context: Context, oldPath: String, newPath: String) {
        try {
            var fileNames = context.assets.list(oldPath) //获取assets目录下的所有文件及目录名，空目录不会存在
            if (fileNames != null) {
                if (fileNames.isNotEmpty()) { // 目录
                    //Log.d("lin.huang", "oldPath1->$oldPath==========================================================")
                    val file = File(newPath);
                    file.mkdirs();//如果文件夹不存在，则递归
                    fileNames.forEach {
                        Log.d("lin.huang", "oldPath1->$oldPath, $it")
                        copyFilesFassets(
                            context,
                            oldPath + File.separator + it,
                            newPath + File.separator + it
                        );
                    }
                } else {// 文件
                    //Log.d("lin.huang", "oldPath2->$oldPath")
                    context.assets.open(oldPath).bufferedReader().use {
                        writeFileContent(newPath, it.readText())
                    }
                    File(newPath).mkdirs()
                }
            }
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
}