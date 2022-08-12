import { convertToRGBAHex } from "./../util";

export class StatusBarFFI implements Plaoc.IStatusBarFFI {
  private _ffi = (globalThis as any).StatusBar as Plaoc.StatusBarDesktopFFI;

  async setStatusBarColor(
    color?: Plaoc.RGBAHex,
    barStyle?: Plaoc.StatusBarStyle
  ): Promise<void> {
    let colorHex: string;
    let darkIcons: Plaoc.StatusBarAndroidStyle;

    if (!color) {
      colorHex = this._ffi.getStatusBarColor();
    } else {
      colorHex = color;
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
      const colorHex = convertToRGBAHex(color);

      resolve(colorHex);
    });
  }

  getStatusBarVisible(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const isVisible = this._ffi.getStatusBarVisible();

      resolve(isVisible);
    });
  }

  async toggleStatusBarVisible(): Promise<void> {
    const isVisible = await this.getStatusBarVisible();

    this._ffi.toggleStatusBarVisible(!isVisible);

    return;
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

  async toggleStatusBarOverlay(): Promise<void> {
    const overlay = await this.getStatusBarOverlay();

    this._ffi.toggleStatusBarOverlay(!overlay);

    return;
  }

  async setStatusBarOverlay(): Promise<void> {
    const overlay = await this.getStatusBarOverlay();

    if (!overlay) {
      await this.toggleStatusBarOverlay();
    }

    return;
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
