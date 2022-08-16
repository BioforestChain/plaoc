import { convertToRGBAHex } from "../util";
import { StatusBar } from "./bfcsStatusBar.type";

async () => {
  // 等到dweb-communication组件注册成功，再执行StatusBarFFI代码
  await customElements.whenDefined("dweb-communication");
};

export class StatusBarFFI implements StatusBar.IStatusBarFFI {
  private _ffi!: StatusBar.StatusBarDesktopFFI;

  constructor() {
    this._ffi = (document.querySelector(
      "dweb-communication"
    ) as unknown) as StatusBar.StatusBarDesktopFFI;
  }

  async setStatusBarColor(
    color?: Color.RGBAHex,
    barStyle?: StatusBar.StatusBarStyle
  ): Promise<void> {
    if (color) {
      await this._ffi.setStatusbarBackgroundColor(color);
    }

    if (barStyle) {
      await this._ffi.setStatusbarStyle(barStyle);
    }

    return;
  }

  async getStatusBarColor(): Promise<Color.RGBAHex> {
    const color = await this._ffi.getStatusbarBackgroundColor();

    const colorHex = convertToRGBAHex(color);

    return colorHex;
  }

  async getStatusBarVisible(): Promise<boolean> {
    const isHidden = await this._ffi.getStatusbarHidden();

    return !isHidden;
  }

  async toggleStatusBarVisible(): Promise<void> {
    const isVisible = await this.getStatusBarVisible();

    await this._ffi.setStatusbarHidden(isVisible);

    return;
  }

  async setStatusBarHidden(): Promise<void> {
    await this._ffi.setStatusbarHidden(true);

    return;
  }

  async getStatusBarOverlay(): Promise<boolean> {
    const isOverlay = await this._ffi.getStatusbarOverlay();

    return isOverlay;
  }

  async toggleStatusBarOverlay(): Promise<void> {
    const isOverlay = await this.getStatusBarOverlay();

    await this._ffi.setStatusbarOverlay(!isOverlay);

    return;
  }

  async setStatusBarOverlay(): Promise<void> {
    await this._ffi.setStatusbarOverlay(true);

    return;
  }

  async getStatusBarStyle(): Promise<StatusBar.StatusBarStyle> {
    const barStyle = await this._ffi.getStatusbarStyle();

    return barStyle;
  }
}
