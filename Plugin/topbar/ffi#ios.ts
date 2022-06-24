export class TopBarFFI {
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

  toggleEnabled(): Promise<void> {
    return new Promise<void>(async (resolve, reject) => {
      const isEnabled = await this.getEnabled();

      this._ffi.hiddenNaviBar.postMessage(isEnabled ? "1" : "0");

      resolve();
    });
  }

  async setHidden(): Promise<void> {
    await this._ffi.hiddenNaviBar.postMessage("1");

    return;
  }

  async getOverlay(): Promise<boolean> {
    const overlay = this._ffi.getNaviOverlay.postMessage(null);

    return overlay;
  }

  toggleOverlay(): Promise<void> {
    return new Promise<void>(async (resolve, reject) => {
      const overlay = await this.getOverlay();

      this._ffi.updateNaviBarAlpha.postMessage(overlay ? "0" : "1");

      resolve();
    });
  }

  setOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateNaviBarAlpha.postMessage("1");

      resolve();
    });
  }

  async getTitle(): Promise<string> {
    const title = this._ffi.getNaviTitle.postMessage(null);

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

    return actionList;
  }

  setActions(actionList: Plaoc.TopBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.customNaviActions.postMessage(actionList);

      resolve();
    });
  }

  async getBackgroundColor(): Promise<string> {
    const colorObject: Plaoc.IColor =
      await this._ffi.getNaviBackgroundColor.postMessage(null);

    // 返回值：#ffffff
    return colorObject.color;
  }

  async setBackgroundColor(colorObject: Plaoc.IColor): Promise<void> {
    this._ffi.updateNaviBarBackgroundColor.postMessage(colorObject);

    return;
  }

  async getForegroundColor(): Promise<string> {
    const colorObject: Plaoc.IColor =
      await this._ffi.getNaviForegroundColor.postMessage(null);

    return colorObject.color;
  }

  async setForegroundColor(colorObject: Plaoc.IColor): Promise<void> {
    this._ffi.updateNaviBarTintColor.postMessage(colorObject);

    return;
  }
}
