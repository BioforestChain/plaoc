package org.bfchain.libappmgr.ui.dcim
/*
import android.net.Uri
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File

@Composable
fun VideScreen() {
  val context = LocalContext.current
  val exoPlayer = SimpleExoPlayer.Builder(context)
    .build()
    .apply {
      playWhenReady = false
    }
  //uri可以时网络url资源，这里我adb push了一个视频到使用sd卡根目录
  val mediaItem = MediaItem.fromUri(Uri.fromFile(File("/storage/emulated/0/DCIM/trailer.mp4")))
  exoPlayer.setMediaItem(mediaItem)
  exoPlayer.prepare()
  exoPlayer.play()
  PlayerSurface(
    modifier = Modifier
      .width(400.dp)
      .height(400.dp)
  ) {
    it.player = exoPlayer
  }
}

@Composable
fun PlayerSurface(
  modifier: Modifier,
  onPlayerViewAvailable: (PlayerView) -> Unit = {}
) {
  AndroidView(
    factory = { context ->
      PlayerView(context).apply {
        useController = true
        onPlayerViewAvailable(this)
      }
    },
    modifier = modifier
  )
}*/
