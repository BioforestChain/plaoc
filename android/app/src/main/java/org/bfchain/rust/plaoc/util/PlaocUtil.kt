package org.bfchain.rust.plaoc.util

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import org.bfchain.rust.plaoc.R
import org.bfchain.rust.plaoc.system.barcode.QRCodeScanningActivity

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
            "org.bfchain.rust.plaoc.qrcodescan",
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
