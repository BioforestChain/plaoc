package org.bfchain.rust.plaoc.system.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.bfchain.rust.plaoc.App
import org.bfchain.rust.plaoc.MainActivity
import org.bfchain.rust.plaoc.R
import org.bfchain.rust.plaoc.system.deeplink.DWebReceiver

class NotifyManager() {
  enum class ChannelType(
    val typeName: String,
    val channelID: String,
    val property: Int,
    val importance: Int
  ) {
    // 默认消息，只需要任务栏有显示即可
    DEFAULT(
      typeName = "默认通知",
      channelID = "plaoc_cid_default",
      property = NotificationCompat.PRIORITY_DEFAULT,
      importance = NotificationManager.IMPORTANCE_DEFAULT,
    ),

    // 紧急消息，需要及时弹出提示
    IMPORTANT(
      typeName = "紧急通知",
      channelID = "plaoc_cid_important",
      property = NotificationCompat.PRIORITY_HIGH,
      importance = NotificationManager.IMPORTANCE_HIGH,
    ),
  }

  companion object {
    private const val TAG: String = "NotifyManager"
    val sInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
      NotifyManager()
    }
    var notifyId = 100 // 消息id，如果notify的id相同时，相当于修改而不是新增
    var requestCode = 1 // 用于通知栏点击时，避免都是点击到最后一个

    fun getDefaultPendingIntent(): PendingIntent {
      var intent = Intent(App.appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      return PendingIntent.getActivity(
        App.appContext, ++requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
      )
    }

    /**
     * 打开DWeb需要通过调用broadcast后，由receiver打开
     */
    fun createBroadcastPendingIntent(appName: String, url: String): PendingIntent {
      var intent = Intent(DWebReceiver.ACTION_OPEN_DWEB).apply {
        putExtra("AppName", appName)
        putExtra("URL", url)
        `package` = App.appContext.packageName
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      return PendingIntent.getBroadcast(
        App.appContext, ++requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
      )
    }
  }

  // 用于创建一个消息通知
  fun createNotification(
    title: String,
    text: String,
    smallIcon: Int = R.mipmap.ic_launcher_round,
    bigText: String = "",
    channelType: ChannelType = ChannelType.DEFAULT,
    intent: PendingIntent = getDefaultPendingIntent()
  ) {
    var context = App.appContext
    var builder = NotificationCompat.Builder(context, channelType.channelID)
      .setSmallIcon(smallIcon)
      .setContentTitle(title)
      .setContentText(text)
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText(bigText)
      )
      .setContentIntent(intent)
      .setPriority(channelType.property)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 创建渠道，针对不同类别的消息进行独立管理
      val channel =
        NotificationChannel(channelType.channelID, channelType.typeName, channelType.importance)
      // Register the channel with the system
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }

    with(NotificationManagerCompat.from(context)) {
      notify(notifyId++, builder.build())
    }
  }
}
