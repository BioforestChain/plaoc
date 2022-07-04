import { convertToRGBAHex } from "@plaoc/plugin-util";

export class BottomBarFFI implements Plaoc.IBottomBarFFI {
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

    return overlay === 1;
  }

  async toggleOverlay(): Promise<void> {
    const overlay = await this.getOverlay();

    this._ffi.updateBottomViewOverlay.postMessage(overlay ? 0 : 1);

    return;
  }

  setOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewOverlay.postMessage(1);

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
      let _actionList: Plaoc.BottomBarItem[] = [];

      // 如果color值设置的是rgba()形式，转化为#开头的十六进制
      for (const item of actionList) {
        if (item.colors) {
          for (const key of Object.keys(item.colors)) {
            let color = item.colors[key as keyof Plaoc.IBottomBarColors];

            if (typeof color === "string") {
              item.colors[key as keyof Plaoc.IBottomBarColors] =
                convertToRGBAHex(color as string) as Plaoc.BottomBarColorType;
            }
          }
        }

        _actionList.push(item);
      }

      this._ffi.customBottomActions.postMessage(actionList);

      resolve();
    });
  }

  async getBackgroundColor(): Promise<Plaoc.RGBAHex> {
    const colorHex: Plaoc.RGBAHex =
      await this._ffi.getBottomBarBackgroundColor.postMessage(null);

    return colorHex;
  }

  async setBackgroundColor(color: Plaoc.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewBackgroundColor.postMessage(color);

      resolve();
    });
  }

  async getForegroundColor(): Promise<Plaoc.RGBAHex> {
    const colorHex: Plaoc.RGBAHex =
      await this._ffi.getBottomViewForegroundColor.postMessage(null);

    return colorHex;
  }

  async setForegroundColor(color: Plaoc.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewForegroundColor.postMessage(color);

      resolve();
    });
  }
}
