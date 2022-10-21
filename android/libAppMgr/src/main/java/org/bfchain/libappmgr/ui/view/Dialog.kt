package org.bfchain.libappmgr.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
        }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)) {
          Text(text = state.customDialog.confirmText.value)
        }
      },
      dismissButton = {
        TextButton(onClick = {
          state.customShow.value = false
          onCancel?.let { onCancel() }
        }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)) {
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
