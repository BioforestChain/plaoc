package org.bfchain.rust.plaoc.system.device

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Build.MODEL
import android.provider.Settings
import android.telephony.TelephonyManager
import com.google.gson.Gson
import org.bfchain.libappmgr.utils.AppContextUtil
import org.bfchain.rust.plaoc.App
import org.bfchain.rust.plaoc.system.device.model.*


data class DeviceData(
  var deviceName: String = "", // 设备名称
  var deviceMode: String = "", // 设备型号
  var deviceVersion: String = "", // 版本号
  var processor: String = "", // 处理器
  var memory: MemoryData? = null, // 运行内存
  var storage: StorageSize? = null, // 存储
  var screen: String = "", // 屏幕
  var phone: String = "", // 手机号码
)

class DeviceInfo {

  fun getAppInfo(): String {
    return AppInfo().getAppInfo()
  }

  fun getBatteryInfo(): String {
    return BatteryInfo().getBatteryInfo()
  }

  fun getDeviceInfo(): String {
    return Gson().toJson(deviceData)
  }

  val deviceData: DeviceData
    get() {
      var deviceData = DeviceData()
      deviceData.deviceName = deviceName
      deviceData.deviceMode = deviceMode
      deviceData.screen = deviceScreen
      deviceData.deviceVersion = deviceVersion
      deviceData.processor = deviceProcessor
      deviceData.phone = devicePhone
      var memoryInfo = MemoryInfo()
      deviceData.memory = memoryInfo.memoryData
      deviceData.storage = memoryInfo.storageSize
      return deviceData
    }

  private lateinit var mTelephonyManager: TelephonyManager

  val deviceName: String
    @SuppressLint("MissingPermission")
    get() {
      mTelephonyManager =
        AppContextUtil.sInstance?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
      // 获取 Global.DEVICE_NAME值不对，所以考虑用蓝牙名称，这二者理论上是一致的
      // 但是如果蓝牙是关闭的，修改设备名称后不会立刻同步到蓝牙，只有等蓝牙打开后才会同步名称
      return Settings.Secure.getString(App.appContext.contentResolver, "bluetooth_name")
    }

  val deviceMode: String
    get() {
      //return Settings.Global.getString(App.appContext.contentResolver, Settings.Global.DEVICE_NAME)
      return MODEL
    }

  val deviceScreen: String
    get() {
      var displayMetrics = App.appContext.resources.displayMetrics
      var screenWith = displayMetrics.widthPixels
      var screenHeight = displayMetrics.heightPixels
      /*var windowManager = App.appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      val dm = DisplayMetrics()
      windowManager.defaultDisplay.getMetrics(dm)*/
      return "$screenHeight * $screenWith"
    }

  val deviceVersion: String
    get() {
      return Build.DISPLAY
    }

  val deviceProcessor: String
    get() {
      return Build.HARDWARE
    }

  val devicePhone: String
    @SuppressLint("MissingPermission")
    get() {
      return mTelephonyManager.line1Number
    }
}
