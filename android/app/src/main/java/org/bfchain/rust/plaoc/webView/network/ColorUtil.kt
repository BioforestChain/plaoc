package org.bfchain.rust.plaoc.webView.network


/**将ARGB color int 转化为RGBA 十六进制*/
fun getColorHex(color: Int): String {
  return ("#" + Integer.toHexString(color))
}

fun hexToIntColor(hexColor:String): Int {
  val hex = hexColor.replace("#","0x");
  return Integer.valueOf(hex, 16)
}


