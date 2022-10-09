package org.bfchain.rust.plaoc.system.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.bfchain.libappmgr.database.AppContract
import org.bfchain.rust.plaoc.App

class PermissionManager : DialogInterface.OnClickListener {
  private val MY_PERMISSIONS = 6666

  private val DEFAULT_DENY_DIALOG_TITLE = "申请权限"
  private val DEFAULT_DENY_DIALOG_POS_BTN = "设置"
  private val DEFAULT_DENY_DIALOG_NEG_BTN = "取消"

  private var activity: Activity? = null
  private var fragment: Fragment? = null

  private var permissionCallback: PermissionCallback? = null

  // private var permission: String? = null
  private var permissions = ArrayList<PermissionStatus>()
  private var showDenyDialog = true
  private var showRationale = true
  private var denyDialogText: String? = null
  private var denyDialogTitle = DEFAULT_DENY_DIALOG_TITLE
  private var denyPosBtnTxt = DEFAULT_DENY_DIALOG_POS_BTN
  private var denyNegBtnTxt = DEFAULT_DENY_DIALOG_NEG_BTN
  private var showNegBtn = true
  private var isCancellable = true

  private var alertDialog: AlertDialog? = null

  constructor(fragment: Fragment) {
    this.fragment = fragment
    this.activity = fragment.activity
  }

  constructor(activity: Activity) {
    this.activity = activity
  }

  /**
   * 这个方法是用来显示权限对话框
   * 将权限字符串与此方法一起传递
   */
  fun withAddPermission(permission: String, appId: String = "bfs-app-id"): PermissionManager {
    this.permissions.add(PermissionStatus(appId, permission))
    // this.permission = permission
    return this
  }

  fun withAddPermissions(permissions: ArrayList<PermissionStatus>): PermissionManager {
    this.permissions.addAll(permissions)
    // this.permission = permission
    return this
  }

  fun withClearPermissions(): PermissionManager {
    this.permissions.clear()
    return this
  }

  /**
   * 如果用户没有选中“不再显示”按钮，您希望向他们显示android对话框，则应调用此选项
   * 一旦用户第一次拒绝权限，就会调用此命令
   */
  fun withRationaleEnabled(showRationale: Boolean): PermissionManager {
    this.showRationale = showRationale
    return this
  }

  /**
   * 如果用户请求权限时，选择了“拒绝”并且“不再显示”时，再次请求权限，这边会判断是否显示自定义的对话框
   */
  fun withDenyDialogEnabled(showDenyDialog: Boolean): PermissionManager {
    this.showDenyDialog = showDenyDialog
    return this
  }

  /**
   * 这是一个选项参数，用于显示自定义对话框的标题。默认情况下，它将是“申请权限”
   */
  fun withDenyDialogTitle(denyDialogTitle: String): PermissionManager {
    this.denyDialogTitle = denyDialogTitle
    return this
  }

  /**
   * 这是一个选项参数，用于显示自定义对话框的内容，用于显示用户为什么需要申请权限
   */
  fun withDenyDialogMsg(denyDialogText: String): PermissionManager {
    this.denyDialogText = denyDialogText
    return this
  }

  /**
   * 这是一个选项参数，用于显示自定义对话框的正按钮文本。
   * 默认情况下，它将是“设置”。单击后，用户将重定向到设置界面
   */
  fun withDenyDialogPosBtnText(denyPosBtnTxt: String): PermissionManager {
    this.denyPosBtnTxt = denyPosBtnTxt
    return this
  }

  /**
   * 这是一个选项参数，用于显示自定义对话框的negative按钮文本。默认为“取消”
   * 这是一个选项参数，用于显示/隐藏negative对话框按钮。这是为了确保用户除了授予权限之外别无选择。
   * 默认情况下，此标志为false。
   */
  fun withDenyDialogNegBtnText(
    denyNegBtnTxt: String,
    showNegBtn: Boolean = true
  ): PermissionManager {
    this.denyNegBtnTxt = denyNegBtnTxt
    this.showNegBtn = showNegBtn
    return this
  }

  /**
   * 这是一个选项参数，用于为用户提供取消权限对话框的选项。如果状态是false表示用户必须授权。
   * 默认情况下，此标志为true。
   */
  fun withDialogCancellable(isCancellable: Boolean): PermissionManager {
    this.isCancellable = isCancellable
    return this
  }

  /**
   * 这是一个选项参数，用于接收权限回调信息
   */
  fun withCallback(permissionCallback: PermissionCallback): PermissionManager {
    this.permissionCallback = permissionCallback
    return this
  }

  /**
   * 只有当权限字符串有效时才会调用Build方法，如果启用showDenyDialog标志，则需要传递对话框文本才能构建权限对话框
   */
  @Synchronized
  fun build() {
    //if (permission == null) {
    if (permissions.size <= 0) {
      throw RuntimeException("You need to set a permission before calling Build method!")
    }

    if (permissionCallback == null) {
      throw RuntimeException("You need to set a permissionCallback before calling Build method!")
    }

    if (showDenyDialog && denyDialogText == null) {
      throw RuntimeException("You need to set a deny Dialog description message before calling Build method!")
    }

    isPermissionsGranted(permissions) // 权限状态更新

    askPermissionDialog()
  }

  @Synchronized
  private fun askPermissionDialog() {
    if (hasStarted) return
    hasStarted = true

    /**
     * 请求权限是fragment还是activity，需要调用不同的方法
     */
    /*fragment?.requestPermissions(arrayOf<String>(permission!!), MY_PERMISSIONS)
      ?: ActivityCompat.requestPermissions(
        activity!!,
        arrayOf<String>(permission!!),
        MY_PERMISSIONS
      )*/

    // 先判断系统权限列表，再判断
    var subPermissions = arrayListOf<String>()
    permissions.forEach { if (!it.SystemGrant) subPermissions.add(it.Permission) }
    if (subPermissions.isNotEmpty()) {
      fragment?.requestPermissions(permissions.toArray() as Array<out String>, MY_PERMISSIONS)
        ?: ActivityCompat.requestPermissions(
          activity!!,
          permissions.toArray() as Array<out String>,
          MY_PERMISSIONS
        )
    } else {
      var subPermissions2 = arrayListOf<PermissionStatus>()
    }
  }

  @Synchronized
  fun handleResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (permissions.isEmpty()) return
    val permission = permissions[0]
    when (requestCode) {
      MY_PERMISSIONS ->
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // 判断已授权，回调授权成功
          hasStarted = false
          permissionCallback?.onPermissionGranted(permissions, grantResults)
        } else if (activity!!.shouldShowRequestPermissionRationale(permission) && showRationale) {
          hasStarted = false
          // 如果用户选择了拒绝授权，并且没有选中“不再显示”，那么就会重复弹出Android授权界面
          askPermissionDialog()
        } else if (showDenyDialog) {
          // 如果用户选择了拒绝授权，并且选中了“不再显示”，那么就会显示自定义对话框，告知用户为什么需要授权，并且有跳转到设置功能。默认是允许显示的
          displayDenyDialog()
        } else {
          // 用户在解释后仍拒绝了该权限。 调用了拒绝的回调
          permissionCallback?.onPermissionDismissed(permission)
        }
    }
  }

  @Synchronized
  private fun displayDenyDialog() {
    val alertDialogBuilder = AlertDialog.Builder(activity!!)
      .setTitle(denyDialogTitle) // 自定义对话框的 标题
      .setMessage(denyDialogText) // 自定义对话框的 说明
      .setCancelable(isCancellable) // 自定义对话框是否可被取消
      .setPositiveButton(denyPosBtnTxt, this) // 自定义对话框确认按钮的响应

    // 默认是显示的，用于用户取消操作
    if (showNegBtn) {
      alertDialogBuilder.setNegativeButton(denyNegBtnTxt, this)
    }

    // 创建对话框
    alertDialog = alertDialogBuilder.show()
  }

  @Synchronized
  private fun displayPermissionDialog() {
    val alertDialogBuilder = AlertDialog.Builder(activity!!)
      .setTitle(denyDialogTitle) // 自定义对话框的 标题
      .setMessage(denyDialogText) // 自定义对话框的 说明
      .setCancelable(isCancellable) // 自定义对话框是否可被取消
      .setPositiveButton(denyPosBtnTxt, this) // 自定义对话框确认按钮的响应

    // 默认是显示的，用于用户取消操作
    if (showNegBtn) {
      alertDialogBuilder.setNegativeButton(denyNegBtnTxt, this)
    }

    // 创建对话框
    alertDialog = alertDialogBuilder.show()
  }

  override fun onClick(dialog: DialogInterface, which: Int) {
    hasStarted = false
    if (alertDialog != null && alertDialog!!.isShowing)
      alertDialog!!.dismiss()

    when (which) {
      DialogInterface.BUTTON_POSITIVE -> permissionCallback!!.onPositiveButtonClicked(dialog, which)
      DialogInterface.BUTTON_NEGATIVE -> permissionCallback!!.onNegativeButtonClicked(dialog, which)
    }
  }

  /**
   * 自定义对话框的回调接口，通过这些接口能够接收权限的状态
   * 这是一个可选参数
   */
  interface PermissionCallback {
    fun onPermissionGranted(permissions: Array<String>, grantResults: IntArray)
    fun onPermissionDismissed(permission: String)
    fun onPositiveButtonClicked(dialog: DialogInterface, which: Int)
    fun onNegativeButtonClicked(dialog: DialogInterface, which: Int)
  }

  companion object {
    private var hasStarted = false

    /**
     * 列出需要动态申请的权限
     */
    // CALENDAR（日历）
    val PERMISSION_READ_CALENDAR = Manifest.permission.READ_CALENDAR
    val PERMISSION_WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR

    // CAMERA（相机）
    val PERMISSION_CAMERA = Manifest.permission.CAMERA

    // CONTACTS（联系人）
    val PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS
    val PERMISSION_WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS
    val PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS

    // LOCATION（位置）
    val PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    val PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    // MICROPHONE（麦克风）
    val PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO

    // PHONE（手机）
    val PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
    val PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE
    val PERMISSION_READ_CALL_LOG = Manifest.permission.READ_CALL_LOG
    val PERMISSION_WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG
    val PERMISSION_ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL
    val PERMISSION_USE_SIP = Manifest.permission.USE_SIP
    val PERMISSION_PROCESS_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS

    // SENSORS（传感器）
    val PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS

    // SMS（短信）
    val PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS
    val PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS
    val PERMISSION_READ_SMS = Manifest.permission.READ_SMS
    val PERMISSION_RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH
    val PERMISSION_RECEIVE_MMS = Manifest.permission.RECEIVE_MMS

    // STORAGE（存储卡）
    val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    /**
     * 判断系统的权限
     */
    @Synchronized
    fun isPermissionGranted(permission: String, appId: String): Boolean {
      return isGrantedBySystem(permission) && isGrantedByApp(permission, appId)
    }

    /**
     * 判断系统的权限
     */
    @Synchronized
    fun isPermissionsGranted(permissions: ArrayList<PermissionStatus>) {
      permissions.forEach { it.updateGrantStatus() }
    }

    /**
     * 判断是否申请过系统权限
     */
    @Synchronized
    private fun isGrantedBySystem(permission: String): Boolean {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        return App.appContext.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
      val hasPermission = ContextCompat.checkSelfPermission(App.appContext, permission)
      return hasPermission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 判断应用是否申请过该权限
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

    @Synchronized
    fun requestPermissions(
      fragment: Fragment, permissions: ArrayList<PermissionStatus>, callback: PermissionCallback
    ) {
      checkAndGrantPermissions(PermissionManager(fragment), permissions, callback)
    }

    @Synchronized
    fun requestPermissions(
      activity: Activity, permissions: ArrayList<PermissionStatus>, callback: PermissionCallback
    ) {
      checkAndGrantPermissions(PermissionManager(activity), permissions, callback)
    }

    @Synchronized
    private fun checkAndGrantPermissions(
      pm: PermissionManager, permissions: ArrayList<PermissionStatus>, callback: PermissionCallback
    ) {
      pm.withAddPermissions(permissions)
      pm.withCallback(callback)
      pm.withDenyDialogTitle("申请权限")
      pm.withDenyDialogMsg("系统检测权限被拒绝，请手动授权，否则程序无法运行")
      pm.withDenyDialogPosBtnText("前往设置")
      pm.withDenyDialogNegBtnText("取消")
      pm.build()
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

    fun PermissionStatus.updateGrantStatus() {
      this.SystemGrant = isGrantedBySystem(this.Permission)
      this.AppGrant = isGrantedByApp(this.Permission, this.AppId)
    }
  }
}

data class PermissionStatus(
  val AppId: String,
  val Permission: String,
  var SystemGrant: Boolean = false,
  var AppGrant: Boolean = false
)
