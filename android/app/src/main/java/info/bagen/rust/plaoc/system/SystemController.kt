package info.bagen.rust.plaoc.system

import android.content.Context
import android.os.Vibrator
import info.bagen.libappmgr.utils.AppContextUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SystemController {

  /**
   * 振动、蜂鸣
   * @param vibrateTime
   */
  @OptIn(DelicateCoroutinesApi::class)
  fun vibrate(
    vibrateTime: Long,
    waitTime: Long,
    duration: Long = 0L,
    isRepeat: Boolean = false
  ) {
    val vibrator = AppContextUtil.sInstance!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
      val repeat = if (isRepeat) 0 else -1
      val pattern = longArrayOf(waitTime, vibrateTime)
      vibrator.vibrate(pattern, repeat)
      if (isRepeat) {
        GlobalScope.launch {
          delay(duration)
          vibrator.cancel()
        }
      }
    }
  }

  fun vibrate(
    vibrateTime: Long
  ) {
    val vibrator = AppContextUtil.sInstance!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
      vibrator.vibrate(vibrateTime)
    }
  }
}
