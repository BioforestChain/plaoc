package org.bfchain.rust.plaoc.system.file

import android.util.Log
import com.king.mlkit.vision.camera.util.LogUtils
import org.bfchain.rust.plaoc.App
import org.bfchain.libappmgr.utils.JsonUtil
import org.bfchain.rust.plaoc.DenoService
import org.bfchain.rust.plaoc.ExportNative
import org.bfchain.rust.plaoc.createBytesFactory
import org.bfchain.rust.plaoc.webView.network.dWebView_host
import java.io.File
import java.util.regex.PatternSyntaxException

// const list: string[] = await fs.ls("./", { // list
//   filter: [{ // 声明筛选方式
//     type: "file",directory
//     name: ["*.ts", "xx.*"]
//   }],
//   recursive: true, // 是否要递归遍历目录，默认是 false
// });


class FileSystem {

  private fun getRootPath(): String {
    return "${App.appContext.dataDir}/system-app/$dWebView_host/home"
  }

  private fun getFileByPath(path: String) : File {
    return File(getRootPath() + File.separator + path)
  }

  private fun checkFilter(filer: LsFilter, file: File): Boolean {
    Log.i("xxxx2: ", filer.toString())
    var rType = true
    filer.type.let {
      Log.i("xxxx3: ", it)
      rType = when (it) {
        "file" -> file.isFile
        "directory" -> file.isDirectory
        else -> false
      }
    }
    var rName = false
    filer.name.forEach { regex ->
      if (regex.toRegex().matches(file.name)) {
        rName = true
        return rType // 找到匹配的正则返回true
      }
    }
    return rType && rName
  }

  fun checkRegex(filer:LsFilter): String {
    try {
      filer.name.forEach { regex ->
        regex.toRegex()
      }
    } catch (e: PatternSyntaxException) {
      val msg = "正则表达式语法错误";
      Log.i("regex", msg);
      return msg
    }
    return "ok"
  }
/**
 * filter：
 * recursive：是否递归遍历目录默认false
 * */
  fun ls(path: String, filter: LsFilter, recursive: Boolean = false) {
    val check = checkRegex(filter);
    if (check != "ok") {
     return createBytesFactory(ExportNative.FileSystemLs,  check)
    }
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
    createBytesFactory(ExportNative.FileSystemLs,  JsonUtil.toJson(fileList))
  }

  fun mkdir(path: String, recursive: Boolean = false) {
    val file = getFileByPath(path)
    var bool = false;
    bool = when (recursive) {
      true -> {
        file.mkdirs()
      }
      false -> {
        file.mkdir()
      }
    }
    createBytesFactory(ExportNative.FileSystemMkdir,  bool.toString())
  }

  fun write(
    path: String,
    content: String,
    append: Boolean = false,
    autoCreate: Boolean = true
  ) {
    val file = getFileByPath(path)
    if (!file.exists() && autoCreate) {
      file.parentFile?.mkdirs()
    }
    try {
      when (append) {
        true -> file.bufferedWriter().append(content)
        false -> file.bufferedWriter().use { out -> out.write(content) }
      }
    } catch (e : Exception) {
      LogUtils.d("write fail -> ${e.message}")
      return createBytesFactory(ExportNative.FileSystemWrite, false.toString())
    }
    createBytesFactory(ExportNative.FileSystemWrite, true.toString())
  }

  fun read(path: String) {
    val file = getFileByPath(path)
    file.bufferedReader().use {
      createBytesFactory(ExportNative.FileSystemRead, it.readText())
    }
  }

  fun rm(path:String, deepDelete: Boolean = true) {
    val file = getFileByPath(path)
    when (deepDelete) {
      true -> file.deleteRecursively()
      false -> file.delete()
    }
    createBytesFactory(ExportNative.FileSystemRm, true.toString())
  }
}


data class FileLs (
  val path: String = "",
  val option: LsOption = LsOption()
)

data class LsOption(
  val filter: LsFilter = LsFilter(),
  val recursive: Boolean = false
)

enum class FileType(var value: String) { FILE(value = "file"), DIRECTORY(value = "directory") }

data class LsFilter(
  val type: String = "",
  val name: List<String> = emptyList()
)

data class FileRead (
  val path: String = "",
)

data class FileWrite (
  val path: String = "",
  val option: WriteOption = WriteOption()
)

data class WriteOption(
  val content: String = "",
  val append: Boolean = false,
  val autoCreate: Boolean = true
)
data class FileRm (
  val path: String = "",
  val option: RmOption = RmOption()
)

data class RmOption(
  val deepDelete: Boolean = true
)
