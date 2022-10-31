package org.bfchain.libappmgr.ui.dcim

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import org.bfchain.libappmgr.R
import org.bfchain.libappmgr.entity.PlayerState
import java.io.File

@Composable
fun VideScreen(dcimVM: DCIMViewModel) {
  Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
      factory = { context ->
        PlayerView(context).apply {
          useController = false // 是否显示控制视图
          resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
          this.player = dcimVM.exoPlayer
          dcimVM.exoPlayer.addAnalyticsListener(object : AnalyticsListener {
            override fun onPlayWhenReadyChanged(
              eventTime: AnalyticsListener.EventTime,
              playWhenReady: Boolean,
              reason: Int
            ) {
              //super.onPlayWhenReadyChanged(eventTime, playWhenReady, reason)
              when (playWhenReady) {
                true -> dcimVM.playerState.value = PlayerState.Playing
                false -> dcimVM.playerState.value = PlayerState.Pause
              }
            }

            override fun onPlaybackStateChanged(
              eventTime: AnalyticsListener.EventTime,
              state: Int
            ) {
              //super.onPlaybackStateChanged(eventTime, state)
              when (state) {
                Player.STATE_ENDED -> dcimVM.playerState.value = PlayerState.END
                else -> null
              }
            }
          })
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center)
    ) { playerView ->
      playerView.player?.apply {
        this.setMediaItem(MediaItem.fromUri(Uri.fromFile(File(dcimVM.dcimInfo.value.path))))
        dcimVM.exoPlayer.prepare()
        dcimVM.exoPlayer.pause() // 为了保证每次点击视频后不主动播放
        dcimVM.playerState.value = PlayerState.Play
      }
    }
    PlayerControlView(dcimVM)
  }
}


@Composable
fun BoxScope.PlayerControlView(dcimVM: DCIMViewModel) {
  Box(modifier = Modifier
    .fillMaxWidth()
    .height(200.dp)
    .align(Alignment.Center)
    .clickable {
      Log.d("lin.huang", "PlayerControlView ->${dcimVM.playerState.value}")
      when (dcimVM.playerState.value) {
        PlayerState.Play -> dcimVM.exoPlayer.play()
        PlayerState.Playing -> dcimVM.exoPlayer.pause()
        PlayerState.Pause -> dcimVM.exoPlayer.play()
        PlayerState.END -> {
          dcimVM.exoPlayer.seekTo(0)
          dcimVM.exoPlayer.play()
          dcimVM.playerState.value = PlayerState.Playing
        }
      }
    }) {
    AsyncImage(
      model = when (dcimVM.playerState.value) {
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
