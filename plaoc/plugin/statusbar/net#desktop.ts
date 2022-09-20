import { Color } from "../types/colorType";
import { convertToRGBAHex } from "../util";
import { StatusBar } from "./bfcsStatusBarType";

async () => {
  // 等到dweb-communication组件注册成功，再执行StatusBarFFI代码
  await customElements.whenDefined("dweb-communication");
};

export class StatusBarFFI implements StatusBar.IStatusBarFFI {
  private _ffi!: StatusBar.StatusBarDesktopFFI;

  constructor() {
    this._ffi = document.querySelector(
      "dweb-communication"
    ) as unknown as StatusBar.StatusBarDesktopFFI;
  }
  setStatusBarVisible(isVer: boolean): Promise<boolean> {
    throw new Error("Method not implemented.");
  }
  getStatusBarIsDark(): Promise<StatusBar.StatusBarStyle> {
    throw new Error("Method not implemented.");
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

  async setStatusBarHidden(): Promise<boolean> {
    return await this._ffi.setStatusbarHidden(true);
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

  async setStatusBarOverlay(): Promise<boolean> {
    return await this._ffi.setStatusbarOverlay(true);
  }

  async getStatusBarStyle(): Promise<StatusBar.StatusBarStyle> {
    const barStyle = await this._ffi.getStatusbarStyle();

    return barStyle;
  }
}
