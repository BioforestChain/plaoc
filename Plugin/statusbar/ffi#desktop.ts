import { convertToRGBAHex } from "@plaoc/plugin-util";

// 等到dweb-communication组件注册成功，再执行StatusBarFFI代码
await customElements.whenDefined("dweb-communication");

export class StatusBarFFI implements Plaoc.IStatusBarFFI {
  private _ffi!: Plaoc.StatusBarDesktopFFI;

  constructor() {
    this._ffi = document.querySelector(
      "dweb-communication"
    ) as unknown as Plaoc.StatusBarDesktopFFI;
  }

  async setStatusBarColor(
    color?: Plaoc.RGBAHex,
    barStyle?: Plaoc.StatusBarStyle
  ): Promise<void> {
    if (color) {
      await this._ffi.setStatusbarBackgroundColor(color);
    }

    if (barStyle) {
      await this._ffi.setStatusbarStyle(barStyle);
    }

    return;
  }

  async getStatusBarColor(): Promise<Plaoc.RGBAHex> {
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

  async getStatusBarStyle(): Promise<Plaoc.StatusBarStyle> {
    const barStyle = await this._ffi.getStatusbarStyle();

    return barStyle;
  }
}
