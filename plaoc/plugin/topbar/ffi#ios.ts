export class TopBarFFI implements Plaoc.ITopBarFFI {
  private _ffi = (window as any).webkit.messageHandlers as Plaoc.TopBarIosFFI;

  back() {
    return new Promise<void>((resolve, reject) => {
      this._ffi.back.postMessage(null);

      resolve();
    });
  }

  async getEnabled(): Promise<boolean> {
    const isHidden = await this._ffi.getNaviEnabled.postMessage(null);

    return !isHidden;
  }

  async toggleEnabled(): Promise<void> {
    const isEnabled = await this.getEnabled();

    this._ffi.hiddenNaviBar.postMessage(isEnabled ? "1" : "0");

    return;
  }

  setHidden(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.hiddenNaviBar.postMessage("1");

      resolve();
    });
  }

  async getOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getNaviOverlay.postMessage(null);

    return overlay === 1;
  }

  async toggleOverlay(): Promise<void> {
    const overlay = await this.getOverlay();

    this._ffi.updateNaviBarOverlay.postMessage(overlay ? 0 : 1);

    return;
  }

  setOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateNaviBarOverlay.postMessage(1);

      resolve();
    });
  }

  async getTitle(): Promise<string> {
    const title = await this._ffi.getNaviTitle.postMessage(null);

    return title;
  }

  setTitle(title: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateTitle.postMessage(title);

      resolve();
    });
  }

  async hasTitle(): Promise<boolean> {
    const has = await this._ffi.hasNaviTitle.postMessage(null);

    return has;
  }

  async getHeight(): Promise<number> {
    const height = await this._ffi.naviHeight.postMessage(null);

    return height;
  }

  async getActions(): Promise<Plaoc.TopBarItem[]> {
    const actionList = await this._ffi.getNaviActions.postMessage(null);

    return JSON.parse(actionList);
  }

  setActions(actionList: Plaoc.TopBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.customNaviActions.postMessage(JSON.stringify(actionList));

      resolve();
    });
  }

  async getBackgroundColor(): Promise<Plaoc.RGBAHex> {
    const colorHex: Plaoc.RGBAHex =
      await this._ffi.getNaviBackgroundColor.postMessage(null);

    // 返回值：#ffffff00
    return colorHex;
  }

  async setBackgroundColor(color: Plaoc.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateNaviBarBackgroundColor.postMessage(color);

      resolve();
    });
  }

  async getForegroundColor(): Promise<Plaoc.RGBAHex> {
    const colorHex: Plaoc.RGBAHex =
      await this._ffi.getNaviForegroundColor.postMessage(null);

    return colorHex;
  }

  async setForegroundColor(color: Plaoc.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateNaviBarTintColor.postMessage(color);

      resolve();
    });
  }
}
