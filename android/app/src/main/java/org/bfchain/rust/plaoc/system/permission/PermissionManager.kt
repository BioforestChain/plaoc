package org.bfchain.rust.plaoc.system.permission

import android.app.Activity
import androidx.fragment.app.Fragment
import org.bfchain.libappmgr.system.permission.PermissionManager
import org.bfchain.libappmgr.system.permission.PermissionUtil

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
}
