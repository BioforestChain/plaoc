package info.bagen.libappmgr.ui.splash

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import com.google.accompanist.pager.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import info.bagen.libappmgr.system.media.MediaType
import info.bagen.libappmgr.utils.AppContextUtil
import info.bagen.libappmgr.utils.FilesUtil
import java.io.File

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SplashView(
  paths: ArrayList<String>,
  activeColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
  inactiveColor: Color = activeColor.copy(ContentAlpha.disabled),
  indicatorWidth: Dp = 8.dp
) {
  val pagerState = rememberPagerState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    HorizontalPager(
      count = paths.size, state = pagerState, modifier = Modifier.fillMaxSize()
    ) { loadPage ->
      val path = paths[loadPage]
      val type = FilesUtil.getFileType(path)
      when (type) {
        MediaType.Video.name -> {
          SplashVideoView(path = path)
        }
        else -> {
          AsyncImage(
            model = path,
            contentDescription = null,
            imageLoader = ImageLoader(AppContextUtil.sInstance!!).newBuilder().components {
              add(SvgDecoder.Factory())
            }.build(),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.TopCenter
          )
        }
      }
    }

    HorizontalPagerIndicator(
      pagerState = pagerState,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(0.dp, 0.dp, 0.dp, 50.dp),
      activeColor = activeColor,
      inactiveColor = inactiveColor,
      indicatorWidth = indicatorWidth
    )
  }
}

@Composable
fun SplashVideoView(path: String) {
  val context = LocalContext.current
  val exoPlayer = remember {
    SimpleExoPlayer.Builder(context).build().apply {
      playWhenReady = false
      setMediaItem(MediaItem.fromUri(Uri.fromFile(File(path))))
    }
  }
  DisposableEffect(Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center),
      factory = { context ->
        PlayerView(context).apply {
          useController = false // 是否显示控制视图
          resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
          this.player = exoPlayer
        }
      },
      update = { playerView ->
        playerView.player?.apply {
          this.prepare()
          this.play()
        }
      })
  }) {
    onDispose {
      exoPlayer.release()
    }
  }
}

