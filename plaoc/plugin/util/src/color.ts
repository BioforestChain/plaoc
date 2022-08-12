// 将RGB 和 Alpha 转化为 ARGB color int
export function getColorInt(
  color: Plaoc.RGBHex,
  alpha: Plaoc.AlphaValueHex
): number {
  return (
    parseInt(color.replace("#", "0x"), 16) + (parseInt("0x" + alpha) << 24)
  );
}

// 将ARGB color int 转化为RGBA 十六进制
export function getColorHex(color: number): Plaoc.RGBAHex {
  let colorHex = new Uint32Array([color])[0].toString(16);

  if (colorHex.length < 8) {
    colorHex = "0".repeat(8 - colorHex.length) + colorHex;
  }

  return ("#" + colorHex.slice(2) + colorHex.slice(0, 2)) as Plaoc.RGBAHex;
}

// 将rgba(r, b, g, a)或#rrbbggaa或#rgba转为#rrbbggaa 十六进制
export function convertToRGBAHex(color: string): Plaoc.RGBAHex {
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
  } else if (color.startsWith("#")) {
    if (color.length === 5) {
      colorHex =
        color.slice(0, 1) +
        color.slice(1, 2).repeat(2) +
        color.slice(2, 3).repeat(2) +
        color.slice(3, 4).repeat(2) +
        color.slice(4, 5).repeat(2);
    } else if (color.length === 9) {
      colorHex = color;
    }
  }

  return (colorHex.length === 9 ? colorHex : color) as Plaoc.RGBAHex;
}
