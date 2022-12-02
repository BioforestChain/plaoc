package info.bagen.rust.plaoc.util

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import info.bagen.rust.plaoc.R
import info.bagen.rust.plaoc.system.barcode.QRCodeScanningActivity

object PlaocUtil {

  /**
   * 增加桌面长按图标时显示的快捷列表
   */
  fun addShortcut(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      var shortcutInfoList = arrayListOf<ShortcutInfoCompat>()
      var builder = ShortcutInfoCompat.Builder(context, "plaoc")
      builder.setShortLabel("扫一扫")
        .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_scan))
        .setIntent(
          Intent(
            "info.bagen.rust.plaoc.qrcodescan",
            null,
            context,
            QRCodeScanningActivity::class.java
          )
        )
      shortcutInfoList.add(builder.build());
      ShortcutManagerCompat.addDynamicShortcuts(context, shortcutInfoList);
    }
  }
}

object UnicodeUtils {
  /**
   * 将字符串转成Unicode编码，包括但不限于中文
   *
   * @param src 原始字符串，包括但不限于中文
   * @return Unicode编码字符串
   */
  fun encode(src: String): String {
    val builder = StringBuilder()
    for (element in src) {
      var s = Integer.toHexString(element.code)
      if (s.length == 2) {// 英文转16进制后只有两位，补全4位
        s = "00$s"
      }
      builder.append("\\u$s")
    }
    return builder.toString()
  }

  /**
   * 解码Unicode字符串，得到原始字符串
   *
   * @param unicode Unicode字符串
   * @return 解码后的原始字符串
   */
  fun decode(hex:Array<Int>): String {
    val builder = StringBuilder()
    hex.forEach {
      builder.append(it.toChar().toString())
    }
    return builder.toString()
  }
}
