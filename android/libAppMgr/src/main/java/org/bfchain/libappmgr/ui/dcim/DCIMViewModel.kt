package org.bfchain.libappmgr.ui.dcim

import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.bfchain.libappmgr.entity.*
import java.io.File

class DCIMViewModel : ViewModel() {
  val showSpinner = mutableStateOf(false) // 用于判断是否显示下拉
  val showViewer = mutableStateOf(false) // 用于判断是否显示大图
  val showPreview = mutableStateOf(false) // 用于判断显示大图时时是否是预览
  val showViewerBar = mutableStateOf(false) // 用于判断大图界面是否显示工具栏和按钮
  val checkedSize = mutableStateOf(0)
  val dcimInfoList = mutableStateListOf<DCIMInfo>() // 用于保存图片列表信息
  val checkedList = mutableStateListOf<DCIMInfo>() // 用于保存选中的图片信息
  val dcimSpinnerList = mutableListOf<DCIMSpinner>() // 用于保存标签列表
  val dcimInfo = mutableStateOf(DCIMInfo("")) // 用于保存当前选中的图片

  var dcimSpinner: DCIMSpinner = DCIMSpinner(name = All, count = 0) // 表示加载的是都有的图片和视频
  var dcimMaps: HashMap<String, ArrayList<DCIMInfo>>? = null
  val exoMaps = hashMapOf<String, ExoPlayerData>()

  private var jobList: ArrayList<Job> = arrayListOf() // 用于保存当前的GlobalScope

  companion object {
    val All = "所有图片和视频"
    val AllPhoto = "所有照片"
    val AllVideo = "所有视频"
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

  fun clearExoPlayerList() {
    exoMaps.iterator().forEach {
      it.value.exoPlayer.release()
    }
    exoMaps.clear()
    jobList.forEach { it.cancel() } // 停止后台继续执行的协程
  }

  fun resetExoPlayerList() {
    exoMaps.iterator().forEach {
      it.value.exoPlayer.seekTo(0)
      it.value.exoPlayer.pause()
      it.value.playerState.value = PlayerState.Play
    }
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
    if (dcimMaps == null) return // 如果是空的，就不执行了
    this.dcimSpinner = dcimSpinner
    this.showSpinner.value = false

    dcimInfoList.clear()
    //checkedList.clear()
    when (dcimSpinner.name) {
      All -> {
        // 先加载都有，然后按照时间排序，然后开始加载视频信息
        // 添加三个顶部特殊标签
        dcimSpinnerList.clear()
        var totalPhotos = 0
        var totalVideos = 0
        dcimMaps!!.iterator().forEach { list ->
          dcimInfoList.addAll(list.value)
          list.value.forEach {
            if (it.type == DCIMType.VIDEO) {
              totalVideos++
            } else {
              totalPhotos++
            }
          }
          //dcimSpinnerList.add(DCIMSpinner(list.value[0], list.key,list.value.size)) // 添加对应的标签
          addToSpinnerList(list.value[0], list.key, list.value.size) // 添加对应的标签
        }
        // 进行排序操作
        dcimInfoList.sortWith(Comparator { o1, o2 -> o2.time.compareTo(o1.time) }) // Collections.sort变种
        addTopSpinnerToList(totalPhotos, totalVideos) // 补充顶部三个下拉
        // 开始刷新时间
        dcimInfoList.forEach {
          it.updateDuration()
        }
        /*jobIOScope.launch {
          var list = arrayListOf<DCIMInfo>()
          list.addAll(dcimInfoList)
          list.forEach {
            if (!isActive) return@launch // 判断当前协程是否被cancel了，如果cancel就直接退出
            it.updateDuration()
          }
        }*/
      }
      AllPhoto -> {
        dcimMaps!!.iterator().forEach { list ->
          list.value.forEach {
            if (it.type != DCIMType.VIDEO) {
              dcimInfoList.add(it)
            }
          }
        }
        // 进行排序操作
        dcimInfoList.sortWith(Comparator { o1, o2 -> o2.time.compareTo(o1.time) }) // Collections.sort变种
      }
      AllVideo -> {
        dcimMaps!!.iterator().forEach { list ->
          list.value.forEach {
            if (it.type == DCIMType.VIDEO) {
              dcimInfoList.add(it)
            }
          }
        }
        // 进行排序操作
        dcimInfoList.sortWith(Comparator { o1, o2 -> o2.time.compareTo(o1.time) }) // Collections.sort变种
      }
      else -> {
        dcimMaps!!.iterator().forEach {
          if (it.key == dcimSpinner.name) {
            dcimInfoList.addAll(it.value)
            // 进行排序操作
            dcimInfoList.sortWith(Comparator { o1, o2 -> o2.time.compareTo(o1.time) }) // Collections.sort变种
            return
          }
        }
      }
    }
  }

  private fun DCIMInfo.updateDuration() {
    var job = GlobalScope.launch {
      if (type == DCIMType.VIDEO) {
        if (!isActive) return@launch // 如果判断当前的协程已经cancel，那么就不要再执行了
        var mmr: MediaMetadataRetriever? = null
        try {
          mmr = MediaMetadataRetriever()
          mmr.setDataSource(path)
          bitmap = mmr.frameAtTime
          duration.value =
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()?.div(1000)
              ?: 0
          // mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
          // title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
          // album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
          // artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
          // bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
          // date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
        } catch (e: Exception) {
          Log.d("DCIMInfo", "fail->$e")
        } finally {
          mmr?.release()
        }
      }
    }
    jobList.add(job)
  }

  private fun addTopSpinnerToList(totalPhotos: Int, totalVideos: Int) {
    var photoAdded = false
    var videoAdded = false
    dcimInfoList.forEachIndexed { index, dcimInfo ->
      if (index == 0 && dcimInfo.type == DCIMType.VIDEO) {
        videoAdded = true
        addToSpinnerList(dcimInfo, All, dcimInfoList.size, 0) // 所有图片和视频
        addToSpinnerList(dcimInfo, AllVideo, totalVideos, 1)  // 所有视频
      } else if (index == 0) {
        photoAdded = true
        addToSpinnerList(dcimInfo, All, dcimInfoList.size, 0) // 所有图片和视频
        addToSpinnerList(dcimInfo, AllPhoto, totalPhotos, 1)  // 所有图片
      } else if (photoAdded && dcimInfo.type == DCIMType.VIDEO) {
        addToSpinnerList(dcimInfo, AllVideo, totalVideos, 2)
        return
      } else if (videoAdded && dcimInfo.type != DCIMType.VIDEO) {
        addToSpinnerList(dcimInfo, AllPhoto, totalPhotos, 1) // 所有图片
        return
      }
    }
  }

  private fun addToSpinnerList(dcimInfo: DCIMInfo, name: String, count: Int, index: Int = -1) {
    GlobalScope.launch {
      var dcimSpinner = DCIMSpinner(name, count = count)
      if (dcimInfo.type == DCIMType.VIDEO) {
        dcimInfo.updateDuration()
        dcimSpinner.path = dcimInfo.bitmap ?: ""
      } else {
        dcimSpinner.path = dcimInfo.path
      }
      if (index >= 0) {
        dcimSpinnerList.add(index, dcimSpinner)
      } else {
        dcimSpinnerList.add(dcimSpinner)
      }
    }
  }

  // 加载图片内容
  fun loadDCIMInfo(
    result: (retMap: HashMap<String, ArrayList<DCIMInfo>>) -> Unit
  ) {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        val maps = hashMapOf<String, ArrayList<DCIMInfo>>()
        val pathDCIM = Environment.getExternalStoragePublicDirectory("DCIM")
        traverseDCIM(pathDCIM.absolutePath, maps)
        val pathPicture = Environment.getExternalStoragePublicDirectory("Pictures")
        traverseDCIM(pathPicture.absolutePath, maps)
        result(maps)
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
