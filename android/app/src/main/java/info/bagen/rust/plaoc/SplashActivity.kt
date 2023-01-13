package info.bagen.rust.plaoc

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import info.bagen.libappmgr.R
import info.bagen.libappmgr.system.permission.EPermission
import info.bagen.libappmgr.system.permission.PermissionUtil
import info.bagen.libappmgr.ui.splash.SplashPrivacyDialog
import info.bagen.libappmgr.utils.getBoolean
import info.bagen.libappmgr.utils.saveBoolean
import info.bagen.rust.plaoc.system.permission.PermissionManager
import info.bagen.rust.plaoc.ui.theme.RustApplicationTheme
import info.bagen.rust.plaoc.webView.openDWebWindow

class SplashActivity : AppCompatActivity() {
  private val keyAppFirstLoad = "App_First_Load"
  private var showDialog = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
  }

  override fun onResume() {
    super.onResume()
    val first = this.getBoolean(keyAppFirstLoad, true)
    if (first) {
      setContent {
        RustApplicationTheme {
          SplashMainView()

          if (showDialog) {
            SplashPrivacyDialog(openHome = {
              if (checkAndRequestPermission()) {
                openHomeActivity()
              } else {
                showDialog = false
              }
            }, openWebView = { url -> openDWebWindow(this, url) }, closeApp = { finish() })
          }
        }
      }
    } else {
      openHomeActivity()
    }
  }

  private fun openHomeActivity() {
    startActivity(Intent(this, MainActivity::class.java))
    this.saveBoolean(keyAppFirstLoad, false)
    finish()
  }

  private fun checkAndRequestPermission(): Boolean {
    val permissions = arrayListOf<String>()
    permissions.add(EPermission.PERMISSION_DEVICE.type)
    permissions.add(EPermission.PERMISSION_STORAGE.type)
    if (!PermissionUtil.isPermissionsGranted(permissions)) {
      PermissionManager.requestPermissions(this, permissions)
      return false
    }
    return true
  }

  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<out String>, grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    PermissionManager.onRequestPermissionsResult(requestCode,
      permissions,
      grantResults,
      this,
      object : info.bagen.libappmgr.system.permission.PermissionManager.PermissionCallback {
        override fun onPermissionGranted(permissions: Array<out String>, grantResults: IntArray) {
          kotlin.run OutLine@{
            grantResults.forEach { grant ->
              if (grant != PackageManager.PERMISSION_GRANTED) {
                return@OutLine
              }
            }
            openHomeActivity()
          }
        }

        override fun onPermissionDismissed(permission: String) { }

        override fun onNegativeButtonClicked(dialog: DialogInterface, which: Int) { finish() }

        override fun onPositiveButtonClicked(dialog: DialogInterface, which: Int) {
          PermissionUtil.openAppSettings()
          showDialog = true
          dialog.dismiss()
        }
      })
  }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun SplashMainView() {
  Column(modifier = Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      val gradient = listOf(
        Color(0xFF71D78E), Color(0xFF548FE3)
      )
      Text(
        text = stringResource(id = R.string.app_name),
        modifier = Modifier.align(Alignment.BottomCenter),
        style = TextStyle(
          brush = Brush.linearGradient(gradient), fontSize = 50.sp
        )
      )
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    )
  }
}
