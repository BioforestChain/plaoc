package org.bfchain.libappmgr.ui.view

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import org.bfchain.libappmgr.entity.DownLoadState

data class MaskProgressMode(
  val show: MutableState<Boolean> = mutableStateOf(false),
  val progress: MutableState<Float> = mutableStateOf(0f),
  val downLoadState: MutableState<DownLoadState> = mutableStateOf(DownLoadState.IDLE)
)

fun rememberMaskProgressMode(): MaskProgressMode {
  return MaskProgressMode()
}

fun MaskProgressMode.show(show: Boolean): MaskProgressMode {
  this.show.value = show
  return this
}

fun MaskProgressMode.progress(progress: Float): MaskProgressMode {
  this.progress.value = progress
  return this
}

fun MaskProgressMode.updateState(state: DownLoadState): MaskProgressMode {
  this.downLoadState.value = state
  when (state) {
    DownLoadState.LOADING -> this.show.value = true
    DownLoadState.PAUSE -> this.show.value = true
    else -> this.show.value = false
  }
  return this
}

private fun Modifier.maskView(): Modifier {
  fillMaxSize()
  padding(3.dp)
  clip(RoundedCornerShape(12.dp))
  background(Color.Black.copy(alpha = 0.6f))
  padding(7.dp)
  return this
}

/**
 * 模仿IOS的下载加载界面
 * 覆盖整个图标，中间显示一个圆环，然后根据下载进度显示白色扇形，直到填充完整
 */
@Composable
fun MaskProgressView(
  cpMode: MaskProgressMode = rememberMaskProgressMode(),
  onClick: (() -> Unit)? = null
) {
  var radius = remember { mutableStateOf(0f) }
  var canvasSize by remember { mutableStateOf(0f) }
  if (cpMode.show.value) {
    Box(
      modifier = Modifier
        .maskView()
        .clickable {
          Log.d("lin.huang", "MaskProgressView -> onClick")
          onClick?.let { onClick() }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        radius.value = size.minDimension / 2f
        canvasSize = size.maxDimension
        val topLeftOffset = Offset(size.width / 2 - radius.value, size.height / 2 - radius.value)
        val strokeWith = Stroke(12f)

        /**
         * 绘制一个白色的圆弧
         */
        drawCircle(
          color = Color.White.copy(0.6f),
          style = strokeWith,
          radius = radius.value
        )

        /**
         * 绘制一个进度条
         */
        drawArc(
          startAngle = -90f, // 0表示3点
          sweepAngle = 360 * cpMode.progress.value,
          color = Color.White.copy(0.6f), // 外围圆弧的颜色
          useCenter = true, // true 表示半径需要填充颜色
          size = size,
          topLeft = topLeftOffset,
          style = Fill // 圆弧宽度
        )
      }
    }
  } else if (radius.value > 0) { // 做一个动态消失操作
    var trigger by remember { mutableStateOf(radius.value) }
    var isFinished by remember { mutableStateOf(false) }

    val animatedRadius by animateFloatAsState(
      targetValue = trigger,
      animationSpec = tween(
        durationMillis = 200,
        easing = LinearEasing
      ),
      finishedListener = {
        isFinished = true
      }
    )

    DisposableEffect(Unit) {
      trigger = canvasSize
      onDispose {}
    }

    if (!isFinished) {
      Box(modifier = Modifier.maskView()) {

        Canvas(modifier = Modifier.fillMaxSize()) {
          translate {
            /**
             * 绘制一个白色的圆弧
             */
            drawCircle(
              color = Color.White.copy(0.6f),
              style = Fill,
              radius = animatedRadius
            )
          }
        }
      }
    }
  }
}

/**
 * 用于盖在图标上面的下载遮罩
 */
@Composable
fun DownloadMaskView() {

}
