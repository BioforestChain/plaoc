import { Color } from "../../types/colorType.ts";

// 将RGB 和 Alpha 转化为 ARGB color int
export function getColorInt(
  color: Color.RGBHex,
  alpha: Color.AlphaValueHex,
): number {
  return (
    parseInt(color.replace("#", "0x"), 16) + (parseInt("0x" + alpha) << 24)
  );
}
// 1111 1111
// 将ARGB color int 转化为RGBA 十六进制
export function getColorHex(color: number): Color.RGBAHex {
  let colorHex = new Uint32Array([color])[0].toString(16);

  if (colorHex.length < 8) {
    colorHex = "0".repeat(8 - colorHex.length) + colorHex;
  }

  return ("#" + colorHex.slice(2) + colorHex.slice(0, 2)) as Color.RGBAHex;
}

/**
 * 将rgba(r, b, g, a)或#rrbbggaa或#rgba转为#rrbbggaa 十六进制
 * @param color 
 * @returns  #rrbbggaa
 */
export function convertToRGBAHex(color: string): Color.RGBAHex {
  let colorHex = "#";

  if (color.startsWith("rgba(")) {
    let colorArr = color.replace("rgba(", "").replace(")", "").split(",");

    for (let [index, item] of colorArr.entries()) {
      if (index === 3) {
        item = `${parseFloat(item) * 255}`;
      }
      let itemHex = Math.round(parseFloat(item)).toString(16);

      if (itemHex.length === 1) {
        itemHex = "0" + itemHex;
      }

      colorHex += itemHex;
    }
  }
  if (color.startsWith("#")) {
    if (color.length === 9) {
      colorHex = color;
    } else {
      color = color.substring(1);
      // 如果是 #f71 或者#f72e这种格式的话,转换为5字符格式
      if (color.length === 4 || color.length === 3) {
        color = color.replace(/(.)/g, "$1$1");
      }
      // 填充成9字符格式，不然android无法渲染
      colorHex += color.padEnd(8, "F");
    }
  }
  return colorHex as Color.RGBAHex;
}

/**
 * 把颜色转换为int类型
 * @param color
 */
export function hexToIntColor(color: string): number {
  color = convertToRGBAHex(color);
  return getColorInt(
    color.slice(0, -2) as Color.RGBHex,
    color.slice(-2) as Color.AlphaValueHex,
  );
}
