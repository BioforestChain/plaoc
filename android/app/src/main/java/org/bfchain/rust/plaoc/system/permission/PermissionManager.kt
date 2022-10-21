package org.bfchain.rust.plaoc.system.permission

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import org.bfchain.rust.plaoc.App
import org.bfchain.rust.plaoc.R

class PermissionManager {
  private val DEFAULT_DENY_DIALOG_TITLE = App.appContext.getString(R.string.permission_deny_title)
  private val DEFAULT_DENY_DIALOG_TEXT = App.appContext.getString(R.string.permission_deny_text)
  private val DEFAULT_DENY_DIALOG_POS_BTN =
    App.appContext.getString(R.string.permission_go_settings)
  private val DEFAULT_DENY_DIALOG_NEG_BTN = App.appContext.getString(R.string.permission_cancel)

  private var activity: Activity? = null
  private var fragment: Fragment? = null

  private lateinit var permissionCallback: PermissionCallback

  // private var permission: String? = null
  private var permissions = ArrayList<String>()
  private var showDenyDialog = true
  private var showRationale = true
  private var denyDialogText = DEFAULT_DENY_DIALOG_TEXT
  private var denyDialogTitle = DEFAULT_DENY_DIALOG_TITLE
  private var denyPosBtnTxt = DEFAULT_DENY_DIALOG_POS_BTN
  private var denyNegBtnTxt = DEFAULT_DENY_DIALOG_NEG_BTN
  private var showNegBtn = true
  private var denyCancellable = true
  private var requestCode = MY_PERMISSIONS

  private var alertDialog: AlertDialog? = null

  constructor(fragment: Fragment) {
    this.fragment = fragment
    this.activity = fragment.activity
  }

  constructor(activity: Activity) {
    this.activity = activity
  }
  fun requestPermissions(
    permissions: ArrayList<String>,
    permissionCallback: PermissionCallback = DefaultPermissionCallback,
    showDenyDialog: Boolean = true,
    showRationale: Boolean = true,
    showNegBtn: Boolean = true,
    denyCancellable: Boolean = true,
    denyDialogTitle: String = DEFAULT_DENY_DIALOG_TITLE,
    denyDialogText: String = DEFAULT_DENY_DIALOG_TEXT,
    denyPosBtnTxt: String = DEFAULT_DENY_DIALOG_POS_BTN,
    denyNegBtnTxt: String = DEFAULT_DENY_DIALOG_NEG_BTN,
    requestCode: Int = MY_PERMISSIONS,
  ) {
    this.permissions = permissions
    this.showDenyDialog = showDenyDialog
    this.showRationale = showRationale
    this.showNegBtn = showNegBtn
    this.denyCancellable = denyCancellable
    this.denyDialogTitle = denyDialogTitle
    this.denyDialogText = denyDialogText
    this.denyPosBtnTxt = denyPosBtnTxt
    this.denyNegBtnTxt = denyNegBtnTxt
    this.permissionCallback = permissionCallback
    this.requestCode = requestCode
    askPermissionDialog()
  }

  @Synchronized
  private fun askPermissionDialog() {
    if (hasStarted) return
    hasStarted = true

    /**
     * 请求权限是fragment还是activity，需要调用不同的方法
     */
    fragment?.requestPermissions(
      PermissionUtil.getActualPermissions(this.permissions).toTypedArray(), requestCode
    ) ?: ActivityCompat.requestPermissions(
      activity!!,
      PermissionUtil.getActualPermissions(this.permissions).toTypedArray(),
      requestCode
    )
  }

  @Synchronized
  fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<out String>, grantResults: IntArray
  ) {
    when (requestCode) {
      this.requestCode -> {
        if (permissions.isNotEmpty() && grantResults.isNotEmpty() && permissions.size == grantResults.size) {
          for (i in 1..permissions.size) {
            if (grantResults[i - 1] != PackageManager.PERMISSION_GRANTED) {
              if (activity!!.shouldShowRequestPermissionRationale(permissions[i - 1]) && showRationale) {
                hasStarted = false
                // 如果用户选择了拒绝授权，并且没有选中“不再显示”，那么就会重复弹出Android授权界面
                askPermissionDialog()
                return
              } else if (showDenyDialog) {
                // 如果用户选择了拒绝授权，并且选中了“不再显示”，那么就会显示自定义对话框，告知用户为什么需要授权，并且有跳转到设置功能。默认是允许显示的
                displayDenyDialog(permissions[i - 1])
                return
              } else {
                hasStarted = false
                // 用户在解释后仍拒绝了该权限。 调用了拒绝的回调
                permissionCallback.onPermissionDismissed(permissions[i - 1])
                return
              }
            }
          }
        }
        hasStarted = false
        permissionCallback.onPermissionGranted(permissions, grantResults)
      }
    }
  }

  /**
   * 如果权限被拒绝并且不在提醒时，根据条件确认是否要弹出当前确认对话框
   */
  @Synchronized
  private fun displayDenyDialog(permission: String) {
    val alertDialogBuilder = AlertDialog.Builder(activity!!)
      .setTitle(denyDialogTitle) // 自定义对话框的 标题
      .setMessage(getDenyDialogText(permission)) // 自定义对话框的 说明
      .setCancelable(denyCancellable) // 自定义对话框是否可被取消
      .setPositiveButton(denyPosBtnTxt) { dialog, which ->
        dialog.dismiss()
        permissionCallback.onPositiveButtonClicked(dialog, which)
      } // 自定义对话框确认按钮的响应
      .setOnDismissListener {
        hasStarted = false
      }

    // 默认是显示的，用于用户取消操作
    if (showNegBtn) {
      alertDialogBuilder.setNegativeButton(denyNegBtnTxt) { dialog, which ->
        dialog.dismiss()
        permissionCallback.onNegativeButtonClicked(dialog, which)
      }
    }

    // 创建对话框
    alertDialog = alertDialogBuilder.show()
  }

  /**
   * 用于获取对话框显示的内容
   */
  @Synchronized
  private fun getDenyDialogText(permission: String): String {
    return when (permission) {
      Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR -> {
        App.appContext.getString(R.string.permission_deny_calendar)
      }
      Manifest.permission.CAMERA -> {
        App.appContext.getString(R.string.permission_deny_camera)
      }
      Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
      Manifest.permission.GET_ACCOUNTS -> {
        App.appContext.getString(R.string.permission_deny_contacts)
      }
      Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> {
        App.appContext.getString(R.string.permission_deny_location)
      }
      Manifest.permission.RECORD_AUDIO -> {
        App.appContext.getString(R.string.permission_deny_record_audio)
      }
      Manifest.permission.READ_PHONE_STATE -> {
        App.appContext.getString(R.string.permission_deny_device)
      }
      Manifest.permission.BODY_SENSORS -> {
        App.appContext.getString(R.string.permission_deny_sensor)
      }
      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
        App.appContext.getString(R.string.permission_deny_storage)
      }
      Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG,
      Manifest.permission.WRITE_CALL_LOG, Manifest.permission.ADD_VOICEMAIL,
      Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS -> {
        App.appContext.getString(R.string.permission_deny_call)
      }
      Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
      Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH,
      Manifest.permission.RECEIVE_MMS -> {
        App.appContext.getString(R.string.permission_deny_sms)
      }
      else -> {
        App.appContext.getString(R.string.permission_deny_text)
      }
    }
  }

  /**
   * 自定义对话框的回调接口，通过这些接口能够接收权限的状态
   * 这是一个可选参数
   */
  interface PermissionCallback {
    fun onPermissionGranted(permissions: Array<out String>, grantResults: IntArray)
    fun onPermissionDismissed(permission: String)
    fun onPositiveButtonClicked(dialog: DialogInterface, which: Int)
    fun onNegativeButtonClicked(dialog: DialogInterface, which: Int)
  }

  object DefaultPermissionCallback : PermissionCallback {
    override fun onPermissionGranted(permissions: Array<out String>, grantResults: IntArray) {
    }

    override fun onPermissionDismissed(permission: String) {
    }

    override fun onPositiveButtonClicked(dialog: DialogInterface, which: Int) {
      PermissionUtil.openAppSettings()
    }

    override fun onNegativeButtonClicked(dialog: DialogInterface, which: Int) {
    }
  }

  companion object {
    private var hasStarted = false
    const val MY_PERMISSIONS = 6666
  }
}

