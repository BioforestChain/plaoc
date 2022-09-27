package org.bfchain.rust.plaoc


import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.UserHandle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.work.WorkManager
import com.google.mlkit.vision.barcode.Barcode
import com.king.app.dialog.AppDialog
import com.king.app.dialog.AppDialogConfig
import com.king.mlkit.vision.barcode.BarcodeDecoder
import com.king.mlkit.vision.camera.CameraScan
import com.king.mlkit.vision.camera.analyze.Analyzer.OnAnalyzeListener
import com.king.mlkit.vision.camera.util.LogUtils
import com.king.mlkit.vision.camera.util.PermissionUtils
import org.bfchain.rust.plaoc.lib.drawRect
import org.bfchain.rust.plaoc.system.barcode.BarcodeScanningActivity
import org.bfchain.rust.plaoc.system.barcode.QRCodeScanningActivity
import org.bfchain.libappmgr.ui.main.Home
import org.bfchain.libappmgr.ui.main.MainActivity
import org.bfchain.rust.plaoc.system.device.DeviceInfo
import org.bfchain.rust.plaoc.ui.theme.RustApplicationTheme
import org.bfchain.rust.plaoc.webView.network.dWebView_host
import org.bfchain.rust.plaoc.webView.network.initMetaData
import org.bfchain.rust.plaoc.webView.network.shakeUrl
import org.bfchain.rust.plaoc.webView.openDWebWindow
import org.bfchain.rust.plaoc.webView.sendToJavaScript
import java.net.URL


val callable_map = mutableMapOf<ExportNative, (data: String) -> Unit>()
val denoService = DenoService()
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
  var isQRCode = false //是否是识别二维码
  fun getContext() = this

  companion object {
    const val REQUEST_CODE_PHOTO = 1
    const val REQUEST_CODE_REQUEST_EXTERNAL_STORAGE = 2
    const val REQUEST_CODE_SCAN_CODE = 3
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // 移除任务，防止重启
    WorkManager.getInstance(App.appContext).cancelAllWorkByTag("DenoRuntime")
    // Log.i("xx","workManager=> ${}")
    this.initSystemFn()
    setContent {
      RustApplicationTheme {
        Box(
          modifier = Modifier
              .fillMaxSize()
              .background(MaterialTheme.colors.primary)
        ) {
          Home() {appId, url ->
            dWebView_host = appId
            LogUtils.d("启动了Ar 扫雷：$dWebView_host--$url")
            createWorker(WorkerNative.valueOf("DenoRuntime"), url)
            /*val loadUrl =
              "${App.appContext.dataDir}/system-app/$it/boot/bfs-service/index.mjs"
            createWorker(WorkerNative.valueOf("DenoRuntime"), loadUrl)*/
          }
        }
      }
    }
  }

  /** 初始化系统函数*/
  private fun initSystemFn() {
    callable_map[ExportNative.OpenQrScanner] = { openScannerActivity() }
    callable_map[ExportNative.BarcodeScanner] = { openBarCodeScannerActivity() }
    callable_map[ExportNative.OpenDWebView] = {
      openDWebViewActivity(it)
    }
    callable_map[ExportNative.InitMetaData] = {
      initMetaData(it)
    }
    callable_map[ExportNative.DenoRuntime] = {
      Log.i(TAG, "哈哈哈哈哈哈哈哈哈哈哈哈")
      denoService.denoRuntime(it)
    }
    callable_map[ExportNative.EvalJsRuntime] =
      { sendToJavaScript(it) }
  }

  // 选择图片后回调到这
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK) {
      when (requestCode) {
        REQUEST_CODE_PHOTO -> processPhoto(data)
        REQUEST_CODE_SCAN_CODE -> processScanResult(data)
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE_REQUEST_EXTERNAL_STORAGE && PermissionUtils.requestPermissionsResult(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        permissions,
        grantResults
      )
    ) {
      startPickPhoto()
    }
  }

  // 扫码后显示一下Toast
  private fun processScanResult(data: Intent?) {
    val text = CameraScan.parseScanResult(data)
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
  }

  // 显示扫码的结果，是显示一张图片
  private fun processPhoto(data: Intent?) {
    data?.let {
      try {
        val src = MediaStore.Images.Media.getBitmap(contentResolver, it.data)
        BarcodeDecoder.process(src, object : OnAnalyzeListener<List<Barcode>?> {
          override fun onSuccess(result: List<Barcode>) {
            if (result.isNotEmpty()) {
              val buffer = StringBuilder()
              val bitmap = src.drawRect { canvas, paint ->
                for ((index, data) in result.withIndex()) {
                  buffer.append("[$index] ").append(data.displayValue)
                    .append("\n")
                  canvas.drawRect(data.boundingBox, paint)
                }
              }

              val config =
                AppDialogConfig(getContext(), R.layout.barcode_result_dialog)
              config.setContent(buffer)
                .setHideCancel(true)
                .setOnClickOk {
                  AppDialog.INSTANCE.dismissDialog()
                }
              val imageView = config.getView<ImageView>(R.id.ivDialogContent)
              imageView.setImageBitmap(bitmap)
              AppDialog.INSTANCE.showDialog(config)
            } else {
              LogUtils.d("result is null")
              Toast.makeText(getContext(), "result is null", Toast.LENGTH_SHORT)
                .show()
            }
          }

          override fun onFailure() {
            LogUtils.d("onFailure")
            Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show()
          }
          //如果指定具体的识别条码类型，速度会更快
        }, if (isQRCode) Barcode.FORMAT_QR_CODE else Barcode.FORMAT_ALL_FORMATS)
      } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(getContext(), e.message, Toast.LENGTH_SHORT).show()
      }

    }
  }

  // 相册的二维码
  private fun pickPhotoClicked(isQRCode: Boolean) {
    this.isQRCode = isQRCode
    if (PermissionUtils.checkPermission(
        getContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE
      )
    ) {
      startPickPhoto()
    } else {
      PermissionUtils.requestPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        REQUEST_CODE_REQUEST_EXTERNAL_STORAGE
      )
    }
  }

  // 打开相册
  private fun startPickPhoto() {
    val pickIntent = Intent(
      Intent.ACTION_PICK,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
    startActivityForResult(pickIntent, REQUEST_CODE_PHOTO)
  }

  // 打开条形码（现在这里的效果是不断扫二维码,还需要修改）
  private fun openBarCodeScannerActivity() {
    startActivityForResult(
      Intent(this, BarcodeScanningActivity::class.java),
      REQUEST_CODE_SCAN_CODE
    )
  }

  // 打开二维码
  private fun openScannerActivity() {
    startActivityForResult(
      Intent(this, QRCodeScanningActivity::class.java),
      REQUEST_CODE_SCAN_CODE
    )
  }

  private fun openDWebViewActivity(path: String) {
    // 存储一下host，用来判断是远程的还是本地的
    if (dWebView_host == "") {
      return
    }
    val url = "https://$dWebView_host.dweb${shakeUrl(path)}"
    LogUtils.d("启动了DWebView:$path")
    openDWebWindow(
      activity = getContext(),
      url = url
    )
  }
}
