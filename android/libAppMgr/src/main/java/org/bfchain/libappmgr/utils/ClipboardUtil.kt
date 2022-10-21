package org.bfchain.libappmgr.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bfchain.libappmgr.entity.AppVersion
import org.bfchain.libappmgr.network.ApiService
import org.bfchain.libappmgr.network.base.BaseData
import org.bfchain.libappmgr.network.base.IApiResult

object ClipboardUtil {

  // 写内容到剪切板
  @SuppressLint("ServiceCast")
  fun writeToClipboard(context: Context, content: String) {
    // 获取剪贴板管理器
    var clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    // 创建普通字符型ClipData
    var clipData = ClipData.newPlainText("OcrText", content)
    // 将ClipData内容放到系统剪贴板里。
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, "复制成功!", Toast.LENGTH_SHORT).show();
  }

  @SuppressLint("ServiceCast")
  fun readFromClipboard(context: Context): String? {
    // 获取剪贴板管理器
    var clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    // 获取剪贴板的剪贴数据集
    var clipData = clipboardManager.primaryClip;
    if (clipData != null && clipData!!.itemCount > 0) {
      return clipData!!.getItemAt(0).text.toString()
    }
    return null
  }

  suspend fun readAndParsingClipboard(context: Context) {
    var content = readFromClipboard(context)
    if (content != null && content!!.startsWith("http")) {
      // 网络请求最新版本
      withContext(Dispatchers.IO) {
        var xx = ApiService.instance.getAppVersion("KEJPMHLA/appversion.json")
        if (xx.isSuccess) {
          var bb = xx.value as BaseData<AppVersion>
          Log.d("lin.huang", "readAndParsingClipboard -> ${bb.data?.version}")
        } else {
          Log.d("lin.huang", "readAndParsingClipboard -> load fail->$xx")
        }
      }
    }
  }
}
