import { getColorInt, getColorHex } from "@plaoc/plugin-util";

export class StatusBarFFI implements Plaoc.IStatusBarFFI {
  private _ffi: Plaoc.StatusBarAndroidFFI = system_ui;

  async setStatusBarColor(
    color?: Plaoc.RGBHex,
    barStyle?: Plaoc.StatusBarStyle
  ): Promise<void> {
    let colorHex: number;
    let darkIcons: Plaoc.StatusBarAndroidStyle;

    if (!color) {
      colorHex = this._ffi.getStatusBarColor();
    } else {
      colorHex = getColorInt(
        color.slice(0, -2) as Plaoc.RGBHex,
        color.slice(-2) as Plaoc.AlphaValueHex
      );
    }

    if (!barStyle) {
      let isDarkIcons = await this.getStatusBarStyle();
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

    this._ffi.setStatusBarColor(colorHex, darkIcons);

    return;
  }

  getStatusBarColor(): Promise<Plaoc.RGBAHex> {
    return new Promise<Plaoc.RGBAHex>((resolve, reject) => {
      const color = this._ffi.getStatusBarColor();
      const colorHex = getColorHex(color);

      resolve(colorHex);
    });
  }

  getStatusBarVisible(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const isVisible = this._ffi.getStatusBarVisible();

      resolve(isVisible);
    });
  }

  toggleStatusBarVisible(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleStatusBarVisible(0);

      resolve();
    });
  }

  async setStatusBarHidden(): Promise<void> {
    const isVisible = await this.getStatusBarVisible();

    if (isVisible) {
      await this.toggleStatusBarVisible();
    }

    return;
  }

  getStatusBarOverlay(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const overlay = this._ffi.getStatusBarOverlay();

      resolve(overlay);
    });
  }

  toggleStatusBarOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleStatusBarOverlay(0);

      resolve();
    });
  }

  setStatusBarOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleStatusBarOverlay(1);

      resolve();
    });
  }

  getStatusBarStyle(): Promise<Plaoc.StatusBarStyle> {
    return new Promise<Plaoc.StatusBarStyle>((resolve, reject) => {
      const isDarkIcons = this._ffi.getStatusBarStyle();
      let barStyle: Plaoc.StatusBarStyle;

      if (isDarkIcons) {
        barStyle = "dark-content" as Plaoc.StatusBarStyle.DARK_CONTENT;
      } else {
        barStyle = "light-content" as Plaoc.StatusBarStyle.LIGHT_CONTENT;
      }

      resolve(barStyle);
    });
  }
}
