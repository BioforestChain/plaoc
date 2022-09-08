package org.bfchain.libappmgr.ui.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class AlertDialogState(
    var showDialog: MutableState<Boolean> = mutableStateOf(false),
    var title: MutableState<String> = mutableStateOf("提示"),
    var content: MutableState<String> = mutableStateOf("内容"),
    var confirmText: MutableState<String> = mutableStateOf("确定"),
    var dismissText: MutableState<String> = mutableStateOf("取消")
)

fun AlertDialogState.changeState(
    showDialog: Boolean? = null,
    title: String? = null,
    content: String? = null,
    confirmText: String? = null,
    dismissText: String? = null
) {
    this.showDialog.value = showDialog?.let { it } ?: this.showDialog.value
    this.title.value = title?.let { it } ?: this.title.value
    this.content.value = content?.let { it } ?: this.content.value
    this.confirmText.value = confirmText?.let { it } ?: this.confirmText.value
    this.dismissText.value = dismissText?.let { it } ?: this.dismissText.value
}

/**
 * 显示普通的提示框
 */
@Composable
fun CustomAlertDialog(
    state: AlertDialogState,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    Log.d("Dialog.kt", "CustomAlertDialog enter")
    if (state.showDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(20.dp),
            onDismissRequest = { state.showDialog.value = false }, // 点击对话框周围空白部分
            title = {
                Text(text = state.title.value)
            },
            text = { Text(text = state.content.value) },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm?.let { onConfirm() } // 回调通知点击了确认按钮
                }) {
                    Text(text = state.confirmText.value)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    state.showDialog.value = false
                    onCancel?.let { onCancel() }
                }) {
                    Text(text = state.dismissText.value)
                }
            })
    }
}

/**
 * 显示加载提示框
 */
@Composable
fun LoadingDialog(showDialog: MutableState<Boolean>, onCancel: (() -> Unit)? = null) {
    Log.d("Dialog.kt", "LoadingDialog enter")
    if (showDialog.value) {
        Dialog(onDismissRequest = {
            showDialog.value = false
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

@Composable
fun ProgressDialog(
    showDialog: MutableState<Boolean>,
    progress: MutableState<Float>,
    onCancel: (() -> Unit)? = null
) {
    if (showDialog.value) {
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
                    CircularProgressIndicator(progress = progress.value)
                    Spacer(modifier = Modifier.width(18.dp))
                    var pp = String.format("%d", (progress.value * 100).toInt())
                    Text(text = "正在加载中，请稍后...$pp %")
                }
            }
        }
    }
}

