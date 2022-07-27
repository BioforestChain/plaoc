import { convertToRGBAHex } from "@plaoc/plugin-util";

// 等到dweb-communication组件注册成功，再执行StatusBarFFI代码
await customElements.whenDefined("dweb-communication");

export class BottomBarFFI implements Plaoc.IBottomBarFFI {
  private _ffi!: Plaoc.BottomBarDesktopFFI;

  constructor() {
    this._ffi = document.querySelector(
      "dweb-communication"
    ) as unknown as Plaoc.BottomBarDesktopFFI;
  }

  async getEnabled(): Promise<boolean> {
    const isHidden = await this._ffi.getBottombarHidden();

    return !isHidden;
  }

  async toggleEnabled(): Promise<void> {
    const isEnabled = await this.getEnabled();

    await this._ffi.setBottombarHidden(isEnabled);

    return;
  }

  async setHidden(): Promise<void> {
    await this._ffi.setBottombarHidden(true);

    return;
  }

  async getOverlay(): Promise<boolean> {
    const isOverlay = await this._ffi.getBottombarOverlay();

    return isOverlay;
  }

  async toggleOverlay(): Promise<void> {
    const isOverlay = await this.getOverlay();

    await this._ffi.setBottombarOverlay(!isOverlay);

    return;
  }

  async setOverlay(): Promise<void> {
    await this._ffi.setBottombarOverlay(true);

    return;
  }

  async getHeight(): Promise<number> {
    const height = await this._ffi.getBottombarHeight();

    return Number(height.replace("px", ""));
  }

  async setHeight(heightDp: number): Promise<void> {
    await this._ffi.setBottombarHeight(`${heightDp}px`);

    return;
  }

  getActions(): Promise<Plaoc.BottomBarItem[]> {
    return new Promise<Plaoc.BottomBarItem[]>((resolve, reject) => {
      resolve([]);
    });
  }

  setActions(actionList: Plaoc.BottomBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      resolve();
    });
  }

  async getBackgroundColor(): Promise<Plaoc.RGBAHex> {
    const color = await this._ffi.getBottombarBackgroundColor();

    const colorHex = convertToRGBAHex(color);

    return colorHex;
  }

  async setBackgroundColor(color: Plaoc.RGBAHex): Promise<void> {
    await this._ffi.setBottombarBackgroundColor(color);

    return;
  }

  async getForegroundColor(): Promise<Plaoc.RGBAHex> {
    return "#ffffffff";
  }

  async setForegroundColor(color: Plaoc.RGBAHex): Promise<void> {
    return;
  }
}
