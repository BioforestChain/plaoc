package org.bfchain.libappmgr.ui.dcim

import android.os.Environment
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.launch
import org.bfchain.libappmgr.entity.*
import org.bfchain.libappmgr.utils.AppContextUtil
import java.io.File

class DCIMViewModel : ViewModel() {
  val showSpinner = mutableStateOf(false) // 用于判断是否显示下拉
  val showViewer = mutableStateOf(false) // 用于判断是否显示大图
  val showViewerBar = mutableStateOf(false) // 用于判断大图界面是否显示工具栏和按钮
  val totalSize = mutableStateOf(0)
  val checkedSize = mutableStateOf(0)
  val dcimInfoList = mutableStateListOf<DCIMInfo>() // 用于保存图片列表信息
  val dcimInfo = mutableStateOf(DCIMInfo("")) // 用于保存当前选中的图片

  val dcimSpinnerList = arrayListOf<DCIMSpinner>() // 用于保存选项列表
  val checkedList = arrayListOf<DCIMInfo>() // 用于保存选中的图片信息

  var dcimSpinner: DCIMSpinner = DCIMSpinner("", "图片和视频", 0) // 表示加载的是都有的图片和视频
  val maps = arrayMapOf<String, ArrayList<DCIMInfo>>()

  val playerState = mutableStateOf(PlayerState.Play)
  val exoPlayer = SimpleExoPlayer.Builder(AppContextUtil.sInstance!!.applicationContext)
    .build()
    .apply {
      playWhenReady = false
    }

  /**
   * 用于接收回调
   * send：有选择图片或者视频，返回具体路径
   * cancel：取消
   */
  interface CallBack {
    fun send(fileList: ArrayList<String>)
    fun cancel()
  }

  var mCallBack: CallBack? = null
  fun setCallback(callback: CallBack) {
    mCallBack = callback
  }

  /**
   * 修改选中列表的数据，由于需要刷新列表和状态，所以统一这边处理
   */
  fun updateCheckedList(dcimInfo: DCIMInfo) {
    if (dcimInfo.checked.value) {
      checkedList.remove(dcimInfo)
    } else {
      checkedList.add(dcimInfo)
    }
    dcimInfo.checked.value = !dcimInfo.checked.value
    checkedSize.value = checkedList.size
    var index = 1
    checkedList.forEach {
      it.index.value = index
      index++
    }
  }

  /**
   * Spinner选择不同的类型的，列表进行重新加载
   */
  fun refreshDCIMInfoList(dcimSpinner: DCIMSpinner) {
    this.dcimSpinner = dcimSpinner
    this.showSpinner.value = false

    dcimInfoList.clear()
    //checkedList.clear()

    maps.iterator().forEach {
      if (it.key == dcimSpinner.name) {
        dcimInfoList.addAll(it.value)
      }
    }
  }

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
  private fun createDCIM(path: String, maps: ArrayMap<String, ArrayList<DCIMInfo>>) {
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
    if (!maps.containsKey(defaultPicture)) maps[defaultPicture] =
      arrayListOf() // 默认先创建一个图片目录，用于保存根目录存在的图片
    var files = File(path).listFiles()
    files?.forEach inLoop@{ file ->
      var name = file.name
      if (name.startsWith(".")) return@inLoop // 判断第一个字符如果是. 不执行当前文件，直接continue
      if (file.isFile) {
        var dcimInfo = MediaFile.createDCIMInfo(file.absolutePath)
        when (dcimInfo.type) {
          DCIMType.VIDEO, DCIMType.IMAGE, DCIMType.GIF -> {
            maps[defaultPicture]!!.add(dcimInfo)
          }
        }
      } else if (file.isDirectory) {
        var list = maps[name] ?: arrayListOf()
        file.walk().iterator().forEach subLoop@{ subFile ->
          if (subFile.name.startsWith(".")) return@subLoop
          if (subFile.isFile) {
            var dcimInfo = MediaFile.createDCIMInfo(subFile.absolutePath)
            when (dcimInfo.type) {
              DCIMType.VIDEO, DCIMType.IMAGE, DCIMType.GIF -> {
                list.add(dcimInfo)
              }
            }
          }
        }
        if (list.isNotEmpty() && !maps.containsKey(name)) {
          maps[name] = list // 判断list不为空，并且不在map中，进行添加
        }
      }
    }
  }
}
