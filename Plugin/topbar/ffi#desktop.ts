import { convertToRGBAHex } from "@plaoc/plugin-util";

// 等到dweb-communication组件注册成功，再执行topBarFFI代码
await customElements.whenDefined("dweb-communication");

export class TopBarFFI implements Plaoc.ITopBarFFI {
  private _ffi: Plaoc.TopBarDesktopFFI;

  constructor() {
    this._ffi = document.querySelector(
      "dweb-communication"
    ) as unknown as Plaoc.TopBarDesktopFFI;
  }

  back(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      // this._ffi.back();
      resolve();
    });
  }

  async getEnabled(): Promise<boolean> {
    const isHidden = await this._ffi.getTopbarHidden();

    return !isHidden;
  }

  async toggleEnabled(): Promise<void> {
    const isEnabled = await this.getEnabled();

    await this._ffi.setTopbarHidden(isEnabled);

    return;
  }

  async setHidden(): Promise<void> {
    await this._ffi.setTopbarHidden(true);

    return;
  }

  async getOverlay(): Promise<boolean> {
    const isOverlay = await this._ffi.getTopbarOverlay();

    return isOverlay;
  }

  async toggleOverlay(): Promise<void> {
    const isOverlay = await this.getOverlay();

    await this._ffi.setTopbarOverlay(!isOverlay);

    return;
  }

  async setOverlay(): Promise<void> {
    await this._ffi.setTopbarOverlay(true);

    return;
  }

  async getTitle(): Promise<string> {
    const title = await this._ffi.getTopbarTitle();

    return title;
  }

  async setTitle(title: string): Promise<void> {
    await this._ffi.setTopbarTitle(title);

    return;
  }

  async hasTitle(): Promise<boolean> {
    const title = await this.getTitle();

    return !!title;
  }

  async getHeight(): Promise<number> {
    const height = await this._ffi.getTopbarHeight();

    return Number(height.replace("px", ""));
  }

  getActions(): Promise<Plaoc.TopBarItem[]> {
    return new Promise<Plaoc.TopBarItem[]>((resolve, reject) => {
      // const actionList = JSON.parse(this._ffi.getActions());

      // resolve(actionList);
      resolve([]);
    });
  }

  setActions(actionList: Plaoc.TopBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      // this._ffi.setActions(JSON.stringify(actionList));

      resolve();
    });
  }

  async getBackgroundColor(): Promise<Plaoc.RGBAHex> {
    const color = await this._ffi.getTopbarBackgroundColor();

    const colorHex = convertToRGBAHex(color);

    return colorHex;
  }

  async setBackgroundColor(color: Plaoc.RGBAHex): Promise<void> {
    await this._ffi.setTopbarBackgroundColor(color);

    return;
  }

  async getForegroundColor(): Promise<Plaoc.RGBAHex> {
    const color = await this._ffi.getTopbarForegroundColor();

    const colorHex = convertToRGBAHex(color);

    return colorHex;
  }

  async setForegroundColor(color: Plaoc.RGBAHex): Promise<void> {
    await this._ffi.setTopbarForegroundColor(color);

    return;
  }
}
