import "../typings";
import { Color } from "../typings/types/color.type";
import { StatusBar } from "./bfcsStatusBar.type";

export class StatusBarFFI implements StatusBar.IStatusBarFFI {
  private _ffi = (window as any).webkit
    .messageHandlers as StatusBar.StatusBarIosFFI;

  async setStatusBarColor(
    color?: Color.RGBAHex,
    barStyle?: StatusBar.StatusBarStyle
  ): Promise<void> {
    if (color) {
      this._ffi.updateStatusBackgroundColor.postMessage(color);
    }

    if (barStyle) {
      let darkIcons: StatusBar.StatusBarIosStyle;

      switch (barStyle) {
        case "light-content":
          darkIcons = "lightContent" as StatusBar.StatusBarIosStyle;
          break;
        default:
          darkIcons = "default" as StatusBar.StatusBarIosStyle;
      }

      await this.setStatusBarStyle(darkIcons);
    }

    return;
  }

  async getStatusBarColor(): Promise<Color.RGBAHex> {
    const backgroundColor = await this._ffi.statusBackgroundColor.postMessage(
      null
    );

    return backgroundColor;
  }

  async getStatusBarVisible(): Promise<boolean> {
    const isVisible = await this._ffi.getStatusBarVisible.postMessage(null);

    return isVisible;
  }

  async toggleStatusBarVisible(): Promise<void> {
    const isVisible = await this.getStatusBarVisible();

    this._ffi.updateStatusHidden.postMessage(isVisible ? "1" : "0");

    return;
  }

  async setStatusBarHidden(): Promise<void> {
    const isVisible = await this.getStatusBarVisible();

    if (isVisible) {
      await this.toggleStatusBarVisible();
    }

    return;
  }

  async getStatusBarOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getStatusBarOverlay.postMessage(null);

    return overlay;
  }

  async toggleStatusBarOverlay(): Promise<void> {
    const overlay = await this.getStatusBarOverlay();

    this._ffi.updateStatusBarOverlay.postMessage(overlay ? 0 : 1);

    return;
  }

  async setStatusBarOverlay(): Promise<void> {
    const overlay = await this.getStatusBarOverlay();

    if (!overlay) {
      await this.toggleStatusBarOverlay();
    }

    return;
  }

  async getStatusBarStyle(): Promise<StatusBar.StatusBarStyle> {
    const barStyle: StatusBar.StatusBarIosStyle = await this._ffi.statusBarStyle.postMessage(
      null
    );

    let darkIcons: StatusBar.StatusBarStyle;

    switch (barStyle) {
      case "lightcontent" as StatusBar.StatusBarIosStyle:
        darkIcons = "light-content" as StatusBar.StatusBarStyle;
        break;
      default:
        darkIcons = "dark-content" as StatusBar.StatusBarStyle;
    }

    return darkIcons;
  }

  private setStatusBarStyle(
    barStyle: StatusBar.StatusBarIosStyle
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateStatusStyle.postMessage(barStyle);

      resolve();
    });
  }
}
