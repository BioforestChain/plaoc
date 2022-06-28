export class BottomBarFFI {
  private _ffi = (window as any).webkit
    .messageHandlers as Plaoc.BottomBarIosFFI;

  async getEnabled(): Promise<boolean> {
    const isHidden = await this._ffi.getBottomBarEnabled.postMessage(null);

    return !isHidden;
  }

  async toggleEnabled(): Promise<void> {
    const isEnabled = await this.getEnabled();

    this._ffi.hiddenBottomView.postMessage(isEnabled ? "1" : "0");

    return;
  }

  setHidden(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.hiddenBottomView.postMessage("1");

      resolve();
    });
  }

  async getOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getBottomBarOverlay.postMessage(null);

    return overlay;
  }

  async toggleOverlay(): Promise<void> {
    const overlay = await this.getOverlay();

    this._ffi.updateBottomViewAlpha.postMessage(overlay ? "0" : "1");

    return;
  }

  setOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewAlpha.postMessage("1");

      resolve();
    });
  }

  async getHeight(): Promise<number> {
    const height = await this._ffi.bottomHeight.postMessage(null);

    return height;
  }

  setHeight(height: number): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewHeight.postMessage(height);

      resolve();
    });
  }

  async getActions(): Promise<Plaoc.BottomBarItem[]> {
    const actionList = await this._ffi.getBottomActions.postMessage(null);

    return actionList;
  }

  setActions(actionList: Plaoc.BottomBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.customBottomActions.postMessage(actionList);

      resolve();
    });
  }

  async getBackgroundColor(): Promise<string> {
    const colorObject: Plaoc.IColor =
      await this._ffi.getBottomBarBackgroundColor.postMessage(null);

    return colorObject.color;
  }

  setBackgroundColor(colorObject: Plaoc.IColor): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewBackgroundColor.postMessage(colorObject);

      resolve();
    });
  }

  async getForegroundColor(): Promise<string> {
    const colorObject: Plaoc.IColor =
      await this._ffi.getBottomViewForegroundColor.postMessage(null);

    return colorObject.color;
  }

  setForegroundColor(colorObject: Plaoc.IColor): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewForegroundColor.postMessage(colorObject);

      resolve();
    });
  }
}
