import { NativeUI } from "../common/nativeHandle";
import { netCallNative } from "../common/network";
import "../typings";
import { Color } from "../typings/types/color.type";
import { getColorInt, getColorHex } from "../util";
import { StatusBar } from "./bfcsStatusBar.type";


export class StatusBarFFI implements StatusBar.IStatusBarFFI {

  async setStatusBarColor(
    color?: Color.RGBAHex,
    barStyle?: StatusBar.StatusBarStyle
  ): Promise<void> {
    let colorHex: number;
    let darkIcons: StatusBar.StatusBarAndroidStyle;

    if (!color) {
      const stringColor = await netCallNative(NativeUI.GetStatusBarColor)
      colorHex = Number(stringColor)
    } else {
      colorHex = getColorInt(
        color.slice(0, -2) as Color.RGBHex,
        color.slice(-2) as Color.AlphaValueHex
      );
    }

    if (!barStyle) {
      let isDarkIcons = await netCallNative(NativeUI.GetStatusBarIsDark)
      darkIcons = isDarkIcons ? 1 : 0;
    } else {
      switch (barStyle) {
        case "light-content":
          darkIcons = -1;
          break;
        case "dark-content":
          darkIcons = 1;
          break;
        default:
          darkIcons = 0;
      }
    }
    netCallNative(NativeUI.SetStatusBarColor, { colorHex, darkIcons })
    return;
  }

  async getStatusBarColor(): Promise<Color.RGBAHex> {
    const stringColor = await netCallNative(NativeUI.GetStatusBarColor) as string
    const colorHex = getColorHex(parseFloat(stringColor));
    return colorHex
  }

  async getStatusBarVisible(): Promise<boolean> {
    const isVisible = await netCallNative(NativeUI.GetStatusBarVisible)
    return Boolean(isVisible)
  }

  async setStatusBarVisible(isVer: boolean): Promise<boolean> {
    const stringVisible = await netCallNative(NativeUI.SetStatusBarVisible, isVer)
    return Boolean(stringVisible)
  }

  async setStatusBarHidden(): Promise<boolean> {
    const isVisible = await this.getStatusBarVisible();
    if (isVisible) {
      await this.setStatusBarVisible(false);
    }
    return isVisible;
  }

  async getStatusBarOverlay(): Promise<boolean> {
    const stringOverlay = await netCallNative(NativeUI.GetStatusBarOverlay)
    return Boolean(stringOverlay);
  }

  async setStatusBarOverlay(isOverlay: boolean): Promise<boolean> {
    const isOver = await netCallNative(NativeUI.SetStatusBarOverlay, isOverlay)
    return Boolean(isOver);
  }


  async getStatusBarIsDark(): Promise<StatusBar.StatusBarStyle> {
    const isDarkIcons = await netCallNative(NativeUI.GetStatusBarIsDark);
    let barStyle: StatusBar.StatusBarStyle;
    if (isDarkIcons) {
      barStyle = "dark-content" as StatusBar.StatusBarStyle.DARK_CONTENT;
    } else {
      barStyle = "light-content" as StatusBar.StatusBarStyle.LIGHT_CONTENT;
    }
    return barStyle
  }
}
