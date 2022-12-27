package info.bagen.rust.plaoc.system.permission

import android.app.Activity
import androidx.fragment.app.Fragment
import info.bagen.libappmgr.system.permission.PermissionManager
import info.bagen.libappmgr.system.permission.PermissionUtil

/**
 * 用于请求网络
 */
object PermissionManager {
  fun requestPermissions(
    activity: Activity,
    list: ArrayList<String>,
    callback: PermissionManager.PermissionCallback = PermissionManager.DefaultPermissionCallback
  ) {
    PermissionManager(activity)
      .requestPermissions(PermissionUtil.getActualPermissions(list), callback)
  }

  fun requestPermissions(
    activity: Activity,
    permission: String,
    callback: PermissionManager.PermissionCallback = PermissionManager.DefaultPermissionCallback
  ) {
    PermissionManager(activity)
      .requestPermissions(PermissionUtil.getActualPermissions(permission), callback)
  }

  fun requestPermissions(
    fragment: Fragment,
    list: ArrayList<String>,
    callback: PermissionManager.PermissionCallback = PermissionManager.DefaultPermissionCallback
  ) {
    PermissionManager(fragment)
      .requestPermissions(PermissionUtil.getActualPermissions(list), callback)
  }

  fun requestPermissions(
    fragment: Fragment,
    permission: String,
    callback: PermissionManager.PermissionCallback = PermissionManager.DefaultPermissionCallback
  ) {
    PermissionManager(fragment)
      .requestPermissions(PermissionUtil.getActualPermissions(permission), callback)
  }

  fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    activity: Activity
  ) {
    PermissionManager(activity).onRequestPermissionsResult(requestCode,permissions,grantResults)
  }
}
