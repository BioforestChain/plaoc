package info.bagen.rust.plaoc

import android.Manifest
import android.app.Application.getProcessName
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import com.github.yitter.contract.IdGeneratorOptions
import com.github.yitter.idgen.YitIdHelper
import com.google.mlkit.vision.barcode.Barcode
import com.king.app.dialog.AppDialog
import com.king.app.dialog.AppDialogConfig
import com.king.mlkit.vision.barcode.BarcodeDecoder
import com.king.mlkit.vision.camera.CameraScan
import com.king.mlkit.vision.camera.analyze.Analyzer.OnAnalyzeListener
import com.king.mlkit.vision.camera.util.LogUtils
import com.king.mlkit.vision.camera.util.PermissionUtils
import info.bagen.libappmgr.ui.app.AppViewModel
import info.bagen.libappmgr.ui.main.Home
import info.bagen.libappmgr.ui.main.MainViewModel
import info.bagen.libappmgr.ui.main.SearchAction
import info.bagen.rust.plaoc.broadcast.BFSBroadcastAction
import info.bagen.rust.plaoc.broadcast.BFSBroadcastReceiver
import info.bagen.rust.plaoc.lib.drawRect
import info.bagen.rust.plaoc.system.barcode.BarcodeScanningActivity
import info.bagen.rust.plaoc.system.barcode.QRCodeScanningActivity
import info.bagen.rust.plaoc.system.initServiceApp
import info.bagen.rust.plaoc.system.initSystemFn
import info.bagen.rust.plaoc.ui.theme.RustApplicationTheme
import info.bagen.rust.plaoc.webView.network.dWebView_host
import info.bagen.rust.plaoc.webView.network.shakeUrl
import info.bagen.rust.plaoc.webView.openDWebWindow
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
  var isQRCode = false //是否是识别二维码
  fun getContext() = this
  private val appViewModel: AppViewModel by viewModel()
  private val mainViewModel: MainViewModel by viewModel()
  private var bfsBroadcastReceiver: BFSBroadcastReceiver? = null

  @JvmName("getAppViewModel1")
  fun getAppViewModel(): AppViewModel {
    return appViewModel
  }

  companion object {
    const val REQUEST_CODE_PHOTO = 1
    const val REQUEST_CODE_REQUEST_EXTERNAL_STORAGE = 2
    const val REQUEST_CODE_SCAN_CODE = 3
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    App.mainActivity = this
    //多个Application创建的问题：
    //避免重复初始化
    //var processName = getProcessName()
    //if (!TextUtils.isEmpty(processName) && processName.equals(packageName)) {
    // 初始化无界面APP
    initServiceApp()
    // 初始化系统函数map
    initSystemFn(this)
    // 初始化id生成
    initYitIdHelper()
    // 初始化广播
    registerBFSBroadcastReceiver()
    setContent {
      RustApplicationTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
        ) {
          Home(mainViewModel, appViewModel,
            onSearchAction = { action, data ->
              LogUtils.d("搜索框内容响应：$action--$data")
              when (action) {
                SearchAction.Search -> {
                  openDWebWindow(this@MainActivity, data)
                }
                SearchAction.OpenCamera -> {
                  openScannerActivity()
                }
              }
            },
            onOpenDWebview = { appId, dAppInfo ->
              //执行初始化
              val pid = android.os.Process.myPid()
              println("app pid = $pid")
              dWebView_host = appId
              LogUtils.d("启动了Ar 扫雷：$dWebView_host--$dAppInfo, isDWeb:${dAppInfo?.isDWeb}")
              dAppInfo?.let { appInfo ->
                if (appInfo.isDWeb) {
                  createWorker(WorkerNative.valueOf("DenoRuntime"), appInfo.dAppUrl)
                } else {
                  openDWebWindow(this@MainActivity, appInfo.url)
                }
              }
            })
        }
      }
    }
    //}
  }

  /** 初始化唯一id*/
  private fun initYitIdHelper() {
    val options = IdGeneratorOptions()
    YitIdHelper.setIdGenerator(options)
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

  /**
   * 注册广播，在 onDestroy 的时候需要手动取消注册
   */
  private fun registerBFSBroadcastReceiver() {
    val intentFilter = IntentFilter()
    intentFilter.addAction(BFSBroadcastAction.BFSInstallApp.action)
    bfsBroadcastReceiver = BFSBroadcastReceiver()
    registerReceiver(bfsBroadcastReceiver, intentFilter)
  }

  private fun unRegisterBFSBroadcastReceiver() {
    bfsBroadcastReceiver?.let { unregisterReceiver(it) }
  }

  override fun onDestroy() {
    super.onDestroy()
    unRegisterBFSBroadcastReceiver()
    App.mainActivity = null
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
                  data.boundingBox?.let { it1 -> canvas.drawRect(it1, paint) }
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
  fun openBarCodeScannerActivity() {
    startActivityForResult(
      Intent(this, BarcodeScanningActivity::class.java),
      REQUEST_CODE_SCAN_CODE
    )
  }

  // 打开二维码
  fun openScannerActivity() {
    startActivityForResult(
      Intent(this, QRCodeScanningActivity::class.java),
      REQUEST_CODE_SCAN_CODE
    )
  }

  fun openDWebViewActivity(path: String) {
    // 存储一下host，用来判断是远程的还是本地的
    if (dWebView_host == "") {
       throw NotFoundException("app host not found!")
    }
    val url = if (path.startsWith("http")) {
        path
    } else {
       "https://${dWebView_host.lowercase(Locale.ROOT)}.dweb${shakeUrl(path)}?_=${Date().time}"
    }
    LogUtils.d("启动了DWebView:$url")
    openDWebWindow(
      activity = getContext(),
      url = url
    )
  }
}
