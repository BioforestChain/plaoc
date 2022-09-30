import { Color } from "../types/colorType.ts";
import { convertToRGBAHex } from "../util/index.ts";
import { BottomBar } from "./bfcsBottomBarType.ts";
import { NativeUI } from "../common/nativeHandle.ts";
import { netCallNative } from "@bfsx/gateway";

export class BottomBarFFI implements BottomBar.IBottomBarFFI {
  private _ffi = (window as any).webkit
    .messageHandlers as BottomBar.BottomBarIosFFI;

  async getHidden(): Promise<boolean> {
    const isHidden = await this._ffi.getBottomBarEnabled.postMessage(null);

    return !isHidden;
  }

  async toggleEnabled(): Promise<void> {
    const isEnabled = await this.getHidden();

    this._ffi.hiddenBottomView.postMessage(isEnabled ? "1" : "0");

    return;
  }

  setHidden(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.hiddenBottomView.postMessage("1");

      resolve();
    });
  }

  async getOverlay(): Promise<number> {
    const overlay = await this._ffi.getBottomBarOverlay.postMessage(null);

    return overlay;
  }

  async toggleOverlay(): Promise<void> {
    const overlay = await this.getOverlay();

    this._ffi.updateBottomViewOverlay.postMessage(overlay ? 0 : 1);

    return;
  }

  async setOverlay(alpha: string): Promise<number> {
    return await netCallNative(NativeUI.SetBottomBarOverlay, alpha);
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

  async getActions(): Promise<BottomBar.BottomBarItem[]> {
    const actionList = await this._ffi.getBottomActions.postMessage(null);

    return JSON.parse(actionList);
  }

  setActions(actionList: BottomBar.BottomBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let _actionList: BottomBar.BottomBarItem[] = [];

      // 如果color值设置的是rgba()形式，转化为#开头的十六进制
      for (const item of actionList) {
        if (item.colors) {
          for (const key of Object.keys(item.colors)) {
            let color = item.colors[key as keyof BottomBar.IBottomBarColors];

            if (typeof color === "string") {
              item.colors[key as keyof BottomBar.IBottomBarColors] =
                convertToRGBAHex(
                  color as string,
                ) as BottomBar.BottomBarColorType;
            }
          }
        }

        _actionList.push(item);
      }

      this._ffi.customBottomActions.postMessage(JSON.stringify(_actionList));

      resolve();
    });
  }

  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const colorHex: Color.RGBAHex = await this._ffi.getBottomBarBackgroundColor
      .postMessage(null);

    return colorHex;
  }

  async setBackgroundColor(color: Color.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewBackgroundColor.postMessage(color);

      resolve();
    });
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    const colorHex: Color.RGBAHex = await this._ffi.getBottomViewForegroundColor
      .postMessage(null);

    return colorHex;
  }

  async setForegroundColor(color: Color.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateBottomViewForegroundColor.postMessage(color);

      resolve();
    });
  }
}
