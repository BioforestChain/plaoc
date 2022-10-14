package org.bfchain.libappmgr.ui.dcim

import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.entity.DCIMInfo
import org.bfchain.libappmgr.entity.DCIMType
import org.bfchain.libappmgr.entity.MediaFile
import java.io.File

class DCIMViewModel : ViewModel() {
  // 加载图片内容
  fun loadDCIMInfo(
    result: (retMap: HashMap<String, ArrayList<DCIMInfo>>) -> Unit
  ) {
    viewModelScope.launch {
      val maps = hashMapOf<String, ArrayList<DCIMInfo>>()
      val pathDCIM = Environment.getExternalStoragePublicDirectory("DCIM")
      traverseDCIM(pathDCIM.absolutePath, maps)
      val pathPicture = Environment.getExternalStoragePublicDirectory("Pictures")
      traverseDCIM(pathPicture.absolutePath, maps)
      result(maps)
    }
  }

  /**
   * 遍历当前目录所有文件
   */
  private fun createDCIM(path: String, maps: HashMap<String, ArrayList<DCIMInfo>>) {
    var defaultPicture = "Pictures"
    if (!maps.containsKey(defaultPicture)) maps[defaultPicture] =
      arrayListOf() // 默认先创建一个图片目录，用于保存根目录存在的图片
    var files = File(path).listFiles()
    files?.forEach inLoop@{ file ->
      var name = file.name
      if (name.startsWith(".")) return@inLoop // 判断第一个字符如果是. 不执行当前文件，直接continue
      if (file.isFile) {
        var type = MediaFile.getDCIMType(file.absolutePath)
        if (type != DCIMType.OTHER) {
          maps[defaultPicture]!!.add(DCIMInfo(file.absolutePath, type))
        }
      } else if (file.isDirectory) {
        var list = maps[name] ?: arrayListOf()
        file.listFiles()?.forEach { subFile ->
          var type = MediaFile.getDCIMType(subFile.absolutePath)
          if (subFile.isFile && type != DCIMType.OTHER) {
            var di = DCIMInfo(subFile.absolutePath, type)
            list.add(di)
          }
        }
        if (list.isNotEmpty() && !maps.containsKey(name)) {
          maps[name] = list // 判断list不为空，并且不在map中，进行添加
        }
      }
    }
  }

  /**
   * 遍历当前目录及其子目录所有文件
   */
  private fun traverseDCIM(path: String, maps: HashMap<String, ArrayList<DCIMInfo>>) {
    var defaultPicture = "Pictures"
    if (!maps.containsKey(defaultPicture)) maps[defaultPicture] = arrayListOf() // 默认先创建一个图片目录，用于保存根目录存在的图片
    var files = File(path).listFiles()
    files?.forEach inLoop@{ file ->
      var name = file.name
      if (name.startsWith(".")) return@inLoop // 判断第一个字符如果是. 不执行当前文件，直接continue
      if (file.isFile) {
        var type = MediaFile.getDCIMType(file.absolutePath)
        when (type) {
          DCIMType.VIDEO -> maps[defaultPicture]!!.add(
            DCIMInfo(file.absolutePath, type, duration = file.getVideoDuration())
          )
          DCIMType.IMAGE, DCIMType.GIF -> maps[defaultPicture]!!.add(
            DCIMInfo(file.absolutePath, type)
          )
        }
      } else if (file.isDirectory) {
        var list = maps[name] ?: arrayListOf()
        file.walk().iterator().forEach subLoop@{ subFile ->
          if (subFile.name.startsWith(".")) return@subLoop
          var type = MediaFile.getDCIMType(subFile.absolutePath)
          if (subFile.isFile && type != DCIMType.OTHER) {
            var di = when (type) {
              DCIMType.VIDEO ->
                DCIMInfo(subFile.absolutePath, type, duration = subFile.getVideoDuration())
              else ->
                DCIMInfo(subFile.absolutePath, type)
            }
            list.add(di)
          }
        }
        if (list.isNotEmpty() && !maps.containsKey(name)) {
          maps[name] = list // 判断list不为空，并且不在map中，进行添加
        }
      }
    }
  }
}

//获取视频时长 单位秒
fun File.getVideoDuration(): Int {
  val mmr = MediaMetadataRetriever()
  mmr.setDataSource(absolutePath)
  return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()?.div(1000) ?: 0
}
