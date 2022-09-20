import { convertToRGBAHex } from "../util";
import { BottomBar } from "./bfcsBottomBarType";
import { Color } from "../types/colorType";

// 等到dweb-communication组件注册成功，再执行StatusBarFFI代码
await customElements.whenDefined("dweb-communication");

export class BottomBarFFI implements BottomBar.IBottomBarFFI {
  private _ffi!: BottomBar.BottomBarDesktopFFI;

  constructor() {
    this._ffi = document.querySelector(
      "dweb-communication"
    ) as unknown as BottomBar.BottomBarDesktopFFI;
  }

  async getHidden(): Promise<boolean> {
    const isHidden = await this._ffi.getBottombarHidden();

    return !isHidden;
  }

  async toggleEnabled(): Promise<void> {
    const isEnabled = await this.getHidden();

    await this._ffi.setBottombarHidden(isEnabled);

    return;
  }

  async setHidden(): Promise<void> {
    await this._ffi.setBottombarHidden(true);

    return;
  }

  async getOverlay(): Promise<number> {
    const isOverlay = await this._ffi.getBottombarOverlay();

    return isOverlay;
  }

  async setOverlay(alpha: string): Promise<number> {
    return await this._ffi.setBottombarOverlay(alpha);
  }

  async getHeight(): Promise<number> {
    const height = await this._ffi.getBottombarHeight();

    return Number(height.replace("px", ""));
  }

  async setHeight(heightDp: number): Promise<void> {
    await this._ffi.setBottombarHeight(`${heightDp}px`);

    return;
  }

  getActions(): Promise<BottomBar.BottomBarItem[]> {
    return new Promise<BottomBar.BottomBarItem[]>((resolve, reject) => {
      resolve([]);
    });
  }

  setActions(actionList: BottomBar.BottomBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      resolve();
    });
  }

  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const color = await this._ffi.getBottombarBackgroundColor();

    const colorHex = convertToRGBAHex(color);

    return colorHex;
  }

  async setBackgroundColor(color: Color.RGBAHex): Promise<void> {
    await this._ffi.setBottombarBackgroundColor(color);

    return;
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    return "#ffffffff";
  }

  async setForegroundColor(color: Color.RGBAHex): Promise<void> {
    return;
  }
}
