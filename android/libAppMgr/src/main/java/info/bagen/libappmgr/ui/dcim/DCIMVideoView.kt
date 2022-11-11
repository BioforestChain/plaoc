package info.bagen.libappmgr.ui.dcim

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import info.bagen.libappmgr.R
import info.bagen.libappmgr.entity.ExoPlayerData
import info.bagen.libappmgr.entity.PlayerState
import java.io.File

@SuppressLint("UnrememberedMutableState")
@Composable
fun VideoScreen(dcimVM: DCIMViewModel, path: String, page: Int) {
  dcimVM.resetExoPlayerList(page) // 重置所有的视频
  var exoPlayerData = dcimVM.exoMaps[page]
  if (exoPlayerData == null) {
    exoPlayerData = ExoPlayerData(
      exoPlayer = SimpleExoPlayer.Builder(LocalContext.current).build()
        .apply { playWhenReady = false },
      playerState = mutableStateOf(PlayerState.Play)
    )
    Log.d("lin.huang", "PlayerControlView -> ${exoPlayerData.exoPlayer}:$page:$path")
    exoPlayerData.exoPlayer.setMediaItem(MediaItem.fromUri(Uri.fromFile(File(path))))
    exoPlayerData.exoPlayer.addAnalyticsListener(object : AnalyticsListener {
      override fun onPlayWhenReadyChanged(
        eventTime: AnalyticsListener.EventTime,
        playWhenReady: Boolean,
        reason: Int
      ) {
        //super.onPlayWhenReadyChanged(eventTime, playWhenReady, reason)
        when (playWhenReady) {
          true -> exoPlayerData.playerState.value = PlayerState.Playing
          false -> exoPlayerData.playerState.value = PlayerState.Pause
        }
      }

      override fun onPlaybackStateChanged(
        eventTime: AnalyticsListener.EventTime,
        state: Int
      ) {
        //super.onPlaybackStateChanged(eventTime, state)
        when (state) {
          Player.STATE_ENDED -> exoPlayerData.playerState.value = PlayerState.END
          else -> null
        }
      }
    })
    dcimVM.exoMaps[page] = exoPlayerData
  }
  Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
      factory = { context ->
        PlayerView(context).apply {
          useController = false // 是否显示控制视图
          resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
          this.player = exoPlayerData!!.exoPlayer
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center)
    ) { playerView ->
      playerView.player?.apply {
        this.prepare()
        this.pause()  // 为了保证每次点击视频后不主动播放
        //dcimVM.exoPlayerList.add(this as SimpleExoPlayer)
      }
    }
    PlayerControlView(exoPlayerData!!)
  }
}


@Composable
fun BoxScope.PlayerControlView(exoPlayerData: ExoPlayerData) {
  Log.d("lin.huang", "PlayerControlView -> ${exoPlayerData.exoPlayer}")
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(200.dp)
      .align(Alignment.Center)
      .clickable(
        onClick = {
          when (exoPlayerData.playerState.value) {
            PlayerState.Play -> exoPlayerData.exoPlayer.play()
            PlayerState.Playing -> exoPlayerData.exoPlayer.pause()
            PlayerState.Pause -> exoPlayerData.exoPlayer.play()
            PlayerState.END -> {
              exoPlayerData.exoPlayer.seekTo(0)
              exoPlayerData.exoPlayer.play()
              exoPlayerData.playerState.value = PlayerState.Playing
            }
          }
        },
        // 去除水波纹效果
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
  ) {
    AsyncImage(
      model = when (exoPlayerData.playerState.value) {
        PlayerState.Play -> R.drawable.ic_player_play
        PlayerState.Pause -> R.drawable.ic_player_pause
        PlayerState.END -> R.drawable.ic_player_replay
        PlayerState.Playing -> null
      },
      contentDescription = null,
      modifier = Modifier
        .size(60.dp)
        .align(Alignment.Center)
    )
  }
}
