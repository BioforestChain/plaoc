package info.bagen.libappmgr.ui.dcim

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.Coil
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import info.bagen.libappmgr.R
import info.bagen.libappmgr.ui.theme.AppMgrTheme
import info.bagen.libappmgr.ui.theme.ColorBackgroundBar
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class DCIMActivity : ComponentActivity() {
  val dcimViewModel: DCIMViewModel by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Setup Coil
    val imageLoader = Coil.imageLoader(applicationContext)
    imageLoader.newBuilder()
      .components {
        if (Build.VERSION.SDK_INT >= 28) {
          add(ImageDecoderDecoder.Factory())
        } else {
          add(GifDecoder.Factory())
        }
        add(SvgDecoder.Factory()) // 加载svg图片
        add(VideoFrameDecoder.Factory()) // 显示视频的某一帧
      }
      .placeholder(
        ActivityCompat.getDrawable(this@DCIMActivity, R.drawable.ic_img_placeholder)
      ) // 占位图片
      //.error(ActivityCompat.getDrawable(this@DCIMActivity, R.drawable.)) // 加载失败图片
      .build()
    Coil.setImageLoader(imageLoader)

    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
      checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    ) {
      requestPermissions(
        arrayOf(
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 666
      )
    } else {
      contentLoad()
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      666 -> contentLoad()
    }
  }

  private fun contentLoad() {
    // window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) // 隐藏顶部状态栏
    window.statusBarColor = ColorBackgroundBar.toArgb() // // 修改状态栏颜色
    dcimViewModel.setCallback(callback = object : DCIMViewModel.CallBack {
      override fun send(fileList: ArrayList<String>) {
        // 接收返回的文件列表地址
        fileList.forEach {
          Log.d("lin.huang", "send -> $it")
        }
        this@DCIMActivity.finish()
      }

      override fun cancel() {
        // 取消操作，等于关闭当前activity
        this@DCIMActivity.finish()
      }
    })
    setContent {
      AppMgrTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(ColorBackgroundBar)
        ) {
          DCIMGreeting(
            onGridClick = { /*window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) */ },
            onViewerClick = {
              showWindowStatusBar(dcimViewModel.showViewerBar.value)
            },
            dcimVM = dcimViewModel
          )
        }
      }
    }
  }

  override fun onBackPressed() {
    if (dcimViewModel.showViewer.value) {
      dcimViewModel.showViewer.value = false
      showWindowStatusBar(true)
    } else {
      super.onBackPressed()
    }
  }

  override fun onDestroy() {
    dcimViewModel.clearExoPlayerList()
    dcimViewModel.clearJobList()
    super.onDestroy()
  }

  private fun showWindowStatusBar(show: Boolean) {
    if (show) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    } else {
      window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
  }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun DCIMGreeting(
  onGridClick: () -> Unit,
  onViewerClick: () -> Unit,
  dcimVM: DCIMViewModel = koinViewModel()
) {
  var model = viewModel() as DCIMViewModel
  LaunchedEffect(Unit) {
    model.loadDCIMInfo { maps ->
      dcimVM.dcimMaps.putAll(maps)
      dcimVM.initDCIMInfoList()
    }
  }
  DCIMView(dcimVM, onGridClick, onViewerClick)
}
