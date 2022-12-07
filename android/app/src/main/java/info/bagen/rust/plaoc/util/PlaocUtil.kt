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

  fun saveZeroBuffKey(req_id:ByteArray):String {
    return "${req_id[0]}-${req_id[1]}"
  }
  fun getZeroBuffKey(req_id:ByteArray):String {
    return "${req_id[0] - 1}-${req_id[1]}"
  }
}
