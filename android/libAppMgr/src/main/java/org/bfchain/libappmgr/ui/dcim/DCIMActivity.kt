package org.bfchain.libappmgr.ui.dcim

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.Coil
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import org.bfchain.libappmgr.entity.DCIMInfo
import org.bfchain.libappmgr.ui.theme.AppMgrTheme
import org.bfchain.libappmgr.utils.AppContextUtil

class DCIMActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Setup Coil
    val imageLoader = Coil.imageLoader(applicationContext)
    imageLoader.newBuilder().components {
      if (Build.VERSION.SDK_INT >= 28) {
        add(ImageDecoderDecoder.Factory())
      } else {
        add(GifDecoder.Factory())
      }
      //add(VideoFrameFileFetcher(applicationContext))
      //add(VideoFrameUriFetcher(applicationContext))
      add(VideoFrameDecoder.Factory())
    }.build()
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
    setContent {
      AppMgrTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
        ) {
          DCIMGreeting(
            onGridClick = { window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) },
            onViewerClick = { window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) })
        }
      }
    }
  }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun DCIMGreeting(onGridClick: () -> Unit, onViewerClick: () -> Unit) {
  var dcimInfo: MutableList<DCIMInfo> = mutableStateListOf()
  var model = viewModel() as DCIMViewModel
  LaunchedEffect(Unit) {
    model.loadDCIMInfo() { maps ->
      dcimInfo.clear()
      maps.iterator().forEach { list ->
        dcimInfo.addAll(list.value)
      }
    }
  }
  DCIMView(dcimInfo, onGridClick, onViewerClick)
}
