package info.bagen.libappmgr.system.media

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Base64
import android.util.Log
import info.bagen.libappmgr.database.MediaDBManager
import info.bagen.libappmgr.utils.BitmapUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

enum class MediaType(name: String) {
  All("All"), Image("Image"), Video("Video"), Svg("Svg"), Gif("gif")
}

data class MediaInfo(
  val id: Int = 0, // 用户做唯一性标识
  val path: String, // 表示文件存放位置
  var type: String = "", // 用于表示当前media类型，Video,Image
  // var mimeType: String = "", // 用于表示当前media类型，video(3gp,mp4,mkv..), image(png,jpg,jpeg,png,svg,gif)
  var duration: Int = 0, // 用于表示视频的时长，默认是0 s
  var time: Int = 0, // 用于表示文件更新时间
  var thumbnail: ByteArray? = null, // 用于显示图片的缩略图，视频的某一祯图片
  var bitmap: ByteArray? = null, // 用于原图数据
)

fun MediaInfo.loadThumbnail() {
  try {
    if (type == MediaType.Video.name) {
      val mmr = MediaMetadataRetriever()
      mmr.setDataSource(path)
      thumbnail = mmr.frameAtTime?.toByteArray()
      duration = mmr.getDurationOfMinute()
      mmr.release()
    } else {
      thumbnail = BitmapUtil.getImageThumbnail(path, 200, 200)?.toByteArray()
    }
  } catch (e: Exception) {
    Log.e("MediaManager", "MediaInfo.loadThumbnail -> path=$path (fail->$e)")
  }
}

fun Bitmap.toByteArray(): ByteArray? {
  var baos = ByteArrayOutputStream()
  compress(Bitmap.CompressFormat.JPEG, 50, baos)
  return baos.toByteArray()
}

fun MediaMetadataRetriever.getDurationOfMinute(): Int {
  return try {
    extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let {
      Integer.parseInt(it) / 1000
    } ?: 0
  } catch (e: Exception) {
    0
  }
}

class MediaManager {

  /**
   * 分页获取Media数据
   * @param page 分页的页数，0表示第一页，以此类推
   * @param pageCount 每页返回的数量，默认每页显示50个
   * @param type 获取的类型，默认是返回所有支持的类型，可选择 Image，Video
   */
  fun getMediaInfoPageList(
    page: Int = 0,
    pageCount: Int = 50,
    type: MediaType = MediaType.All
  ): ArrayList<MediaInfo> {
    return MediaDBManager.queryMediaData(
      type = type,
      from = page * pageCount,
      to = (page + 1) * pageCount
    )
  }

  /**
   * 根据具体的id，获取原图
   */
  @SuppressLint("Range")
  fun getOriginMediaData(id: Int): String? {
    // 查询数据库，然后获取path，转为base64
    val mediaInfo = MediaDBManager.queryMediaData(id)
    return mediaInfo?.let { getOriginMediaData(it.type) }
  }

  /**
   * 根据具体的path，获取原图
   */
  fun getOriginMediaData(path: String): String? {
    // 根据path，转为base64
    try {
      var fis = FileInputStream(path)
      var buffer = ByteArray(1024 * 4)
      var baos = ByteArrayOutputStream()
      while (fis.read(buffer) >= 0) {
        baos.write(buffer)
      }
      fis.close()
      return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
    }
    return null
  }
}
