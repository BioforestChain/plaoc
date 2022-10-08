package org.bfchain.rust.plaoc.system.file

import com.king.mlkit.vision.camera.util.LogUtils
import org.bfchain.libappmgr.utils.JsonUtil
import org.bfchain.rust.plaoc.App
import org.bfchain.rust.plaoc.webView.network.dWebView_host
import java.io.File

// const list: string[] = await fs.ls("./", { // list
//   filter: [{ // 声明筛选方式
//     type: "file",directory
//     name: ["*.ts", "xx.*"]
//   }],
//   recursive: true, // 是否要递归遍历目录，默认是 false
// });

enum class FileType(var value: String) { FILE(value = "file"), DIRECTORY(value = "directory") }

data class FileFilter(
  val type: FileType,
  val name: List<String>
)

class FileSystem {

  private fun getRootPath(): String {
    return "${App.appContext.dataDir}/system-app/$dWebView_host/home"
  }

  private fun getFileByPath(path: String) : File {
    return File(getRootPath() + File.separator + path)
  }

  private fun checkFilter(filer: String?, file: File): Boolean {
    checkNotNull(filer)
    val fileFilter = JsonUtil.fromJson(FileFilter::class.java, filer)
    var rType = true
    fileFilter?.type?.let {
      rType = when (it) {
        FileType.FILE -> file.isFile
        FileType.DIRECTORY -> file.isDirectory
      }
    }
    var rName = true
    fileFilter?.name?.let { names ->
      rName = false
      names.forEach { regex ->
        if (regex.toRegex().matches(file.name)) {
          return rType // 找到匹配的正则返回true
        }
      }
    }
    return rType && rName
  }
/**
 * filter：
 * recursive：是否递归遍历目录默认false
 * */
  fun ls(path: String, filter: String? = null, recursive: Boolean = false): String {
    val rootPath = getRootPath()
    val file = File(rootPath + File.separator + path)
    val fileList = arrayListOf<String>()
    when (recursive) {
      true -> { // 遍历获取
        file.walk().iterator().forEach {
          if (checkFilter(filter, it)) {
            fileList.add(it.absolutePath.replace(rootPath, ""))
          }
        }
      }
      false -> {
        file.listFiles()?.forEach {
          if (checkFilter(filter, it)) {
            fileList.add(it.absolutePath.replace(rootPath, ""))
          }
        }
      }
    }
    return JsonUtil.toJson(fileList)
  }

  fun mkdir(path: String, recursive: Boolean = false): Boolean {
    var file = getFileByPath(path)
    return when (recursive) {
      true -> file.mkdirs()
      false -> file.mkdir()
    }
  }

  fun write(
    path: String,
    content: String,
    append: Boolean = false,
    autoCreate: Boolean = true
  ): Boolean {
    val file = getFileByPath(path)
    if (!file.exists() && autoCreate) {
      file.parentFile.mkdirs()
    }
    try {
      when (append) {
        true -> file.bufferedWriter().append(content)
        false -> file.bufferedWriter().use { out -> out.write(content) }
      }
    } catch (e : Exception) {
      LogUtils.d("write fail -> ${e.message}")
      return false
    }
    return true
  }

  fun read(path: String): String {
    val file = getFileByPath(path)
    return file.bufferedReader().use { it.readText() }
  }

  fun rm(path:String, deepDelete: Boolean = true): Boolean {
    val file = getFileByPath(path)
    when (deepDelete) {
      true -> file.deleteRecursively()
      false -> file.delete()
    }
    return true
  }
}
