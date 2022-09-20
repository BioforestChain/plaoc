package org.bfchain.libappmgr.ui.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.bfchain.libappmgr.data.DialogShowOrHide
import org.bfchain.libappmgr.data.rememberDialogAllState

/**
 * 显示普通的提示框
 */
@Composable
fun CustomAlertDialog(
  state: DialogShowOrHide = rememberDialogAllState(),
  onConfirm: (() -> Unit)? = null,
  onCancel: (() -> Unit)? = null
) {
  if (state.customShow.value) {
    AlertDialog(
      modifier = Modifier.padding(20.dp),
      onDismissRequest = { state.customShow.value = false }, // 点击对话框周围空白部分
      title = {
        Text(text = state.customDialog.title.value)
      },
      text = { Text(text = state.customDialog.content.value) },
      confirmButton = {
        TextButton(onClick = {
          onConfirm?.let { onConfirm() } // 回调通知点击了确认按钮
        }) {
          Text(text = state.customDialog.confirmText.value)
        }
      },
      dismissButton = {
        TextButton(onClick = {
          state.customShow.value = false
          onCancel?.let { onCancel() }
        }) {
          Text(text = state.customDialog.dismissText.value)
        }
      })
  }
}

/**
 * 显示加载提示框
 */
@Composable
fun LoadingDialog(
  state: DialogShowOrHide = rememberDialogAllState(),
  onCancel: (() -> Unit)? = null
) {
  if (state.loadingShow.value) {
    Dialog(onDismissRequest = {
      state.loadingShow.value = false
      onCancel?.let { onCancel() }
    }) {
      Box(
        modifier = Modifier
          .height(120.dp)
          .fillMaxWidth()
          .padding(20.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color.White)
      ) {
        Row(
          modifier = Modifier
            .padding(10.dp)
            .align(Alignment.CenterStart),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 左边显示 加载动画，右边显示“正在加载中，请稍后..."
          CircularProgressIndicator()
          Spacer(modifier = Modifier.width(22.dp))
          Text(text = "正在加载中，请稍后...")
        }
      }
    }
  }
}

/**
 * 转圈圈的下载进度显示
 */
@Composable
fun ProgressDialog(
  state: DialogShowOrHide = rememberDialogAllState(),
  onCancel: (() -> Unit)? = null
) {
  if (state.progressShow.value) {
    Dialog(onDismissRequest = {}) {
      Box(
        modifier = Modifier
          .height(120.dp)
          .fillMaxWidth()
          .padding(20.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color.White)
      ) {
        Row(
          modifier = Modifier
            .padding(10.dp)
            .align(Alignment.CenterStart),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 左边显示 加载动画，右边显示“正在加载中，请稍后..."
          CircularProgressIndicator(progress = state.progressDialog.progress.value)
          Spacer(modifier = Modifier.width(18.dp))
          var pro = String.format("%02d", (state.progressDialog.progress.value * 100).toInt())
          Text(text = "正在加载中，请稍后... $pro%")
        }
      }
    }
  }
}

/**
 * 模仿IOS的下载加载界面
 * 覆盖整个图标，中间显示一个圆环，然后根据下载进度显示白色扇形，直到填充完整
 */
@Composable
fun CircleProgressView(
  state: DialogShowOrHide = rememberDialogAllState(),
  onCancel: (() -> Unit)? = null
) {
  var radius = remember { mutableStateOf(0f) }
  var canvasSize by remember { mutableStateOf(0f) }
  if (state.circleProgressShow.value) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.6f))
        .padding(10.dp)
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
          sweepAngle = 360 * state.progressDialog.progress.value,
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
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.6f))
          .padding(10.dp)
      ) {

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
