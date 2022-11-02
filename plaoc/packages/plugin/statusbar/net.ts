import { NativeUI } from "../common/nativeHandle.ts";
// import { getCallNativeUi } from "@bfsx/gateway";
import { Color } from "../types/colorType.ts";
import { StatusBar } from "./bfcsStatusBarType.ts";

export class StatusBarNet implements StatusBar.IStatusBarNet {
  async setStatusBarColor(
    colorHex?: Color.RGBAHex,
    barStyle?: StatusBar.StatusBarStyle
  ): Promise<void> {
    let darkIcons: boolean;

    if (!colorHex) {
      await getCallNativeUi(NativeUI.GetStatusBarColor)
        .then((res) => {
          colorHex = res;
        })
        .catch((_err) => {
          colorHex = "#fff"; // 适配ios没有设置statsBar的情况
        });
    }

    if (!barStyle) {
      const isDarkIcons = await getCallNativeUi(NativeUI.GetStatusBarIsDark);
      darkIcons = isDarkIcons;
    } else {
      switch (barStyle) {
        case "light-content":
          darkIcons = false;
          break;
        case "dark-content":
          darkIcons = true;
          break;
        default:
          darkIcons = false;
      }
    }
    getCallNativeUi(NativeUI.SetStatusBarColor, { colorHex, darkIcons });
    return;
  }

  async getStatusBarColor(): Promise<Color.RGBAHex> {
    const colorHex = await getCallNativeUi(NativeUI.GetStatusBarColor);
    return colorHex;
  }

  async getStatusBarVisible(): Promise<boolean> {
    const isVisible = await getCallNativeUi(NativeUI.GetStatusBarVisible);
    return Boolean(isVisible);
  }

  async setStatusBarVisible(isVer: boolean): Promise<boolean> {
    const stringVisible = await getCallNativeUi(
      NativeUI.SetStatusBarVisible,
      isVer
    );
    return Boolean(stringVisible);
  }

  async setStatusBarHidden(): Promise<boolean> {
    const isVisible = await this.getStatusBarVisible();
    if (isVisible) {
      await this.setStatusBarVisible(false);
    }
    return isVisible;
  }

  async getStatusBarOverlay(): Promise<boolean> {
    const stringOverlay = await getCallNativeUi(NativeUI.GetStatusBarOverlay);
    return Boolean(stringOverlay);
  }

  async setStatusBarOverlay(isOverlay: boolean): Promise<boolean> {
    const isOver = await getCallNativeUi(
      NativeUI.SetStatusBarOverlay,
      isOverlay
    );
    return Boolean(isOver);
  }

  /**
   * 是否是深色
   * @returns
   */
  async getStatusBarIsDark(): Promise<StatusBar.StatusBarStyle> {
    const isDarkIcons = await getCallNativeUi(NativeUI.GetStatusBarIsDark);
    let barStyle: StatusBar.StatusBarStyle;
    if (isDarkIcons) {
      barStyle = "dark-content" as StatusBar.StatusBarStyle.DARK_CONTENT;
    } else {
      barStyle = "light-content" as StatusBar.StatusBarStyle.LIGHT_CONTENT;
    }
    return barStyle;
  }
}
