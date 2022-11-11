package info.bagen.libappmgr.ui.dcim

import android.os.Environment
import android.util.Log
import androidx.collection.arrayMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import info.bagen.libappmgr.data.PreferencesHelper
import info.bagen.libappmgr.database.MediaDBManager
import info.bagen.libappmgr.entity.*
import info.bagen.libappmgr.system.media.MediaType
import kotlinx.coroutines.*
import java.io.File

class DCIMViewModel : ViewModel() {
  val showSpinner = mutableStateOf(false) // 用于判断是否显示下拉
  val showViewer = mutableStateOf(false) // 用于判断是否显示大图
  val showPreview = mutableStateOf(false) // 用于判断显示大图时时是否是预览
  val showViewerBar = mutableStateOf(true) // 用于判断大图界面是否显示工具栏和按钮
  val dcimInfoList = mutableStateListOf<DCIMInfo>() // 用于保存图片列表信息
  val checkedList = mutableStateListOf<DCIMInfo>() // 用于保存选中的图片信息
  val dcimSpinnerList = mutableStateListOf<DCIMSpinner>() // 用于保存标签列表
  val dcimInfo = mutableStateOf(DCIMInfo("")) // 用于保存当前选中的图片

  var dcimSpinner: DCIMSpinner = DCIMSpinner(name = All) // 表示加载的是都有的图片和视频
  var dcimMaps: HashMap<String, ArrayList<DCIMInfo>> = hashMapOf()
  val exoMaps = hashMapOf<Int, ExoPlayerData>()

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

  fun clearJobList() {
    jobList.forEach { it.cancel() } // 停止后台继续执行的协程
    dcimMaps.clear()
  }

  fun clearExoPlayerList() {
    exoMaps.iterator().forEach {
      it.value.exoPlayer.release()
    }
    exoMaps.clear()
  }

  fun resetExoPlayerList(page: Int) {
    synchronized(exoMaps) {
      exoMaps[page - 4]?.exoPlayer?.release()
      exoMaps[page + 4]?.exoPlayer?.release()
      exoMaps.remove(page - 4)
      exoMaps.remove(page + 4)
      exoMaps.iterator().forEach {
        it.value.exoPlayer.seekTo(0)
        it.value.exoPlayer.pause()
        it.value.playerState.value = PlayerState.Play
      }
    }
    /*var iterator = exoMaps.entries.iterator()
    while (iterator.hasNext()) {
      var entry = iterator.next()
      if (entry.key >= page - 3 && entry.key <= page + 3) {
        entry.value.exoPlayer.seekTo(0)
        entry.value.exoPlayer.pause()
        entry.value.playerState.value = PlayerState.Play
      } else {
        Log.d("lin.huang", "resetExoPlayerList1111 page->$page, size=${exoMaps.size}, delete_key=${entry.key}")
        entry.value.exoPlayer.release()
        iterator.remove()
      }
    }*/
    //Log.d("lin.huang", "resetExoPlayerList2222 page->$page, size=${exoMaps.size}")
  }

  /**
   * 修改选中列表的数据，由于需要刷新列表和状态，所以统一这边处理
   */
  fun updateCheckedList(dcimInfo: DCIMInfo? = null) {
    dcimInfo?.let {
      if (it.checked.value) {
        checkedList.remove(it)
      } else {
        checkedList.add(it)
      }
      it.checked.value = !it.checked.value
    }
    var index = 1
    checkedList.forEach {
      it.index.value = index
      index++
    }
  }

  /**
   * 初始化数据
   */
  fun initDCIMInfoList() {
    if (dcimMaps.isEmpty()) return
    dcimInfoList.clear()
    addToSpinnerList(All)
    addToSpinnerList(AllPhoto)
    addToSpinnerList(AllVideo)
    dcimMaps.iterator().forEach { list ->
      dcimInfoList.addAll(list.value)
      addToSpinnerList(name = list.key, count = list.value.size, dcimInfo = list.value[0])
    }
    // 进行排序操作
    dcimInfoList.sortWith { o1, o2 -> o2.time.compareTo(o1.time) } // Collections.sort变种
    // 初始化Spinner
    initSpinnerList()
    if (!PreferencesHelper.isMediaLoading()) {
      // 开始刷新时间
      val list = arrayListOf<DCIMInfo>()
      list.addAll(dcimInfoList)
      list.forEach {
        if (it.type == DCIMType.VIDEO && it.bitmap == null) {
          val job = GlobalScope.launch(Dispatchers.Default) {
            it.updateDuration()
          }
          jobList.add(job)
        }
      }
    }
  }

  fun initSpinnerList() {
    if (dcimInfoList.isEmpty()) return
    var totalPhotos = 0
    var totalVideos = 0
    var firstPhoto: DCIMInfo? = null
    var firstVideo: DCIMInfo? = null
    dcimInfoList.forEach {
      if (it.type == DCIMType.VIDEO) {
        if (firstVideo == null) {
          firstVideo = it
          it.updateDuration()
        }
        totalVideos++
      } else {
        if (firstPhoto == null) {
          firstPhoto = it
          it.updateDuration()
        }
        totalPhotos++
      }
    }
    dcimSpinnerList[0].path.value = dcimInfoList[0].bitmap ?: dcimInfoList[0].path
    dcimSpinnerList[0].count.value = dcimInfoList.size
    firstPhoto?.let {
      dcimSpinnerList[1].path.value = it.path
      dcimSpinnerList[1].count.value = totalPhotos
    }
    firstVideo?.let {
      dcimSpinnerList[2].path.value = it.bitmap ?: it.path
      dcimSpinnerList[2].count.value = totalVideos
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
    when (dcimSpinner.name) {
      All -> {
        // 先加载都有，然后按照时间排序，然后开始加载视频信息
        // 添加三个顶部特殊标签
        dcimMaps.iterator().forEach { list ->
          dcimInfoList.addAll(list.value)
        }
        // 进行排序操作
        dcimInfoList.sortWith { o1, o2 -> o2.time.compareTo(o1.time) } // Collections.sort变种
      }
      AllPhoto -> {
        dcimMaps.iterator().forEach { list ->
          list.value.forEach {
            if (it.type != DCIMType.VIDEO) {
              dcimInfoList.add(it)
            }
          }
        }
        // 进行排序操作
        dcimInfoList.sortWith { o1, o2 -> o2.time.compareTo(o1.time) } // Collections.sort变种
      }
      AllVideo -> {
        dcimMaps.iterator().forEach { list ->
          list.value.forEach {
            if (it.type == DCIMType.VIDEO) {
              dcimInfoList.add(it)
            }
          }
        }
        // 进行排序操作
        dcimInfoList.sortWith { o1, o2 -> o2.time.compareTo(o1.time) } // Collections.sort变种
      }
      else -> {
        dcimMaps.iterator().forEach {
          if (it.key == dcimSpinner.name) {
            dcimInfoList.addAll(it.value)
            // 进行排序操作
            dcimInfoList.sortWith { o1, o2 -> o2.time.compareTo(o1.time) } // Collections.sort变种
            return
          }
        }
      }
    }
  }

  private fun addToSpinnerList(name: String, count: Int = 0, dcimInfo: DCIMInfo? = null) {
    var dcimSpinner = DCIMSpinner(
      name = name,
      count = mutableStateOf(count),
      path = mutableStateOf(dcimInfo?.path ?: "")
    )
    dcimSpinnerList.add(dcimSpinner)
    /*GlobalScope.launch (Dispatchers.IO) {
      if (dcimInfo.type == DCIMType.VIDEO) {
        dcimInfo.updateDuration()
        dcimSpinner.path.value = dcimInfo.bitmap ?: ""
      }
    }*/
  }

  // 加载图片内容
  suspend fun loadDCIMInfo(result: (retMap: HashMap<String, ArrayList<DCIMInfo>>) -> Unit) {
    withContext(Dispatchers.IO) {
      val maps = hashMapOf<String, ArrayList<DCIMInfo>>()
      if (PreferencesHelper.isMediaLoading()) { // 如果后台加载完毕，使用后台的数据
        MediaDBManager.getMediaFilter().forEach { name ->
          val list = arrayListOf<DCIMInfo>()
          MediaDBManager.queryMediaData(filter = name).forEach { mediaInfo ->
            val dcimInfo = DCIMInfo(
              path = mediaInfo.path,
              id = mediaInfo.id,
              type = when (mediaInfo.type) {
                MediaType.Video.name -> DCIMType.VIDEO
                else -> DCIMType.IMAGE
              },
              time = mediaInfo.time,
              duration = mutableStateOf(mediaInfo.duration),
              bitmap = mediaInfo.thumbnail
            )
            list.add(dcimInfo)
          }
          maps[name] = list
        }
      } else { // 如果后台没有加载完成，先自行加载
        val pathDCIM = Environment.getExternalStoragePublicDirectory("DCIM")
        traverseDCIM(pathDCIM.absolutePath, maps)
        val pathPicture = Environment.getExternalStoragePublicDirectory("Pictures")
        traverseDCIM(pathPicture.absolutePath, maps)
      }
      result(maps)
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
