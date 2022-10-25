package org.bfchain.rust.plaoc.system.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.king.mlkit.vision.camera.util.LogUtils
import org.bfchain.libappmgr.database.AppContract
import org.bfchain.rust.plaoc.App

object PermissionUtil {
  const val PERMISSION_CALENDAR = "org.bfchain.rust.plaoc.CALENDAR" // 日历
  const val PERMISSION_CAMERA = "org.bfchain.rust.plaoc.CAMERA" // 相机相册
  const val PERMISSION_CONTACTS = "org.bfchain.rust.plaoc.CONTACTS" // 联系人
  const val PERMISSION_LOCATION = "org.bfchain.rust.plaoc.LOCATION" // 位置
  const val PERMISSION_RECORD_AUDIO = "org.bfchain.rust.plaoc.RECORD_AUDIO" // 录音
  const val PERMISSION_BODY_SENSORS = "org.bfchain.rust.plaoc.BODY_SENSORS" // 传感器（重力，陀螺仪）
  const val PERMISSION_STORAGE = "org.bfchain.rust.plaoc.STORAGE" // 存储
  const val PERMISSION_SMS = "org.bfchain.rust.plaoc.SMS" // 短信
  const val PERMISSION_CALL = "org.bfchain.rust.plaoc.CALL" // 电话
  const val PERMISSION_DEVICE = "org.bfchain.rust.plaoc.DEVICE" // （手机状态）

  /**
   * 判断是否申请过系统权限
   */
  @Synchronized
  fun isPermissionsGranted(permission: String): Boolean {
    var permissionList = getActualPermissions(permission)
    permissionList.forEach { pm ->
      if (!isPermissionGranted(pm)) return false
    }
    return true
  }

  @Synchronized
  fun isPermissionsGranted(permissions: ArrayList<String>): Boolean  {
    var permissionList = getActualPermissions(permissions)
    permissionList.forEach { pm ->
      if (!isPermissionGranted(pm)) return false
    }
    return true
  }

  /**
   * 打开App设置界面
   */
  fun openAppSettings() {
    val i = Intent()
    i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    i.addCategory(Intent.CATEGORY_DEFAULT)
    i.data = Uri.parse("package:" + App.appContext.packageName)
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    App.appContext.startActivity(i)
  }

  @Synchronized
  private fun isPermissionGranted(permission: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
      return App.appContext.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    val hasPermission = ContextCompat.checkSelfPermission(App.appContext, permission)
    return hasPermission == PackageManager.PERMISSION_GRANTED
  }


  /**
   * 判断应用是否申请过权限,,这个本来是应用权限独立管理。暂时舍弃
   */
  @SuppressLint("Range")
  @Synchronized
  private fun isGrantedByApp(permission: String, appId: String): Boolean {
    var project = arrayOf(
      AppContract.Permissions.COLUMN_APP_ID,
      AppContract.Permissions.COLUMN_PERMISSION,
      AppContract.Permissions.COLUMN_GRANT
    )
    var select =
      "${AppContract.Permissions.COLUMN_APP_ID}=? AND ${AppContract.Permissions.COLUMN_PERMISSION}=?"
    var selectArgs = arrayOf(appId, permission)
    var cursor = App.appContext.contentResolver.query(
      AppContract.Permissions.CONTENT_URI, project, select, selectArgs, null
    )
    cursor?.let {
      if (it.moveToFirst()) {
        var grant = it.getInt(it.getColumnIndex(AppContract.Permissions.COLUMN_GRANT))
        return grant == PackageManager.PERMISSION_GRANTED // -1 表示未授权，0表示授权
      }
      it.close()
    }
    return false
  }

  fun getActualPermissions(permission: String) : ArrayList<String> {
    val actualPermissions = arrayListOf<String>()
    when (permission) {
      PERMISSION_CAMERA -> actualPermissions.add(Manifest.permission.CAMERA)
      PERMISSION_RECORD_AUDIO -> actualPermissions.add(Manifest.permission.RECORD_AUDIO)
      PERMISSION_BODY_SENSORS -> actualPermissions.add(Manifest.permission.BODY_SENSORS)
      PERMISSION_DEVICE -> actualPermissions.add(Manifest.permission.READ_PHONE_STATE)
      PERMISSION_CALENDAR -> {
        actualPermissions.add(Manifest.permission.READ_CALENDAR)
        actualPermissions.add(Manifest.permission.WRITE_CALENDAR)
      }
      PERMISSION_CONTACTS -> {
        actualPermissions.add(Manifest.permission.READ_CONTACTS)
        actualPermissions.add(Manifest.permission.WRITE_CONTACTS)
        actualPermissions.add(Manifest.permission.GET_ACCOUNTS)
      }
      PERMISSION_LOCATION -> {
        actualPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        actualPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
      }
      PERMISSION_STORAGE -> {
        actualPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        actualPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
      }
      PERMISSION_SMS -> {
        actualPermissions.add(Manifest.permission.SEND_SMS)
        actualPermissions.add(Manifest.permission.RECEIVE_SMS)
        actualPermissions.add(Manifest.permission.READ_SMS)
        actualPermissions.add(Manifest.permission.RECEIVE_WAP_PUSH)
        actualPermissions.add(Manifest.permission.RECEIVE_MMS)
      }
      PERMISSION_CALL -> {
        actualPermissions.add(Manifest.permission.CALL_PHONE)
        actualPermissions.add(Manifest.permission.USE_SIP)
        actualPermissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
        actualPermissions.add(Manifest.permission.ADD_VOICEMAIL)
        actualPermissions.add(Manifest.permission.READ_CALL_LOG)
        actualPermissions.add(Manifest.permission.WRITE_CALL_LOG)
      }
    }
    LogUtils.d("getActualPermissions -> ${actualPermissions.size}")
    return actualPermissions
  }

  fun getActualPermissions(permissions: ArrayList<String>): ArrayList<String> {
    val actualPermissions = arrayListOf<String>()
    permissions.forEach { permission ->
      var temp = getActualPermissions(permission)
      if (temp.size > 0) actualPermissions.addAll(temp)
    }
    LogUtils.d("getActualPermissions -> ${actualPermissions.size}->${permissions.size}")
    return actualPermissions
  }

  fun testRequestAllPermissions(activity: Activity) {
    var permissions = arrayListOf<String>()
    permissions.add(Manifest.permission.READ_CALENDAR)
    permissions.add(Manifest.permission.WRITE_CALENDAR)
    permissions.add(Manifest.permission.CAMERA)
    permissions.add(Manifest.permission.READ_CONTACTS)
    permissions.add(Manifest.permission.WRITE_CONTACTS)
    permissions.add(Manifest.permission.GET_ACCOUNTS)
    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    permissions.add(Manifest.permission.RECORD_AUDIO)
    permissions.add(Manifest.permission.READ_PHONE_STATE)
    permissions.add(Manifest.permission.CALL_PHONE)
    permissions.add(Manifest.permission.READ_CALL_LOG)
    permissions.add(Manifest.permission.WRITE_CALL_LOG)
    permissions.add(Manifest.permission.ADD_VOICEMAIL)
    permissions.add(Manifest.permission.USE_SIP)
    permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
    permissions.add(Manifest.permission.BODY_SENSORS)
    permissions.add(Manifest.permission.SEND_SMS)
    permissions.add(Manifest.permission.RECEIVE_SMS)
    permissions.add(Manifest.permission.READ_SMS)
    permissions.add(Manifest.permission.RECEIVE_WAP_PUSH)
    permissions.add(Manifest.permission.RECEIVE_MMS)
    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(activity!!, permissions.toTypedArray(),
      PermissionManager.MY_PERMISSIONS
    )
  }
}
