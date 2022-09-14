import { NativeUI } from "../common/nativeHandle";
import { netCallNative } from "../common/network";
import { Color } from "../typings/types/color.type";
import { getColorInt, getColorHex, convertToRGBAHex } from "../util";
import { BottomBar } from "./bfcsBottomBar.type";
export class BottomBarFFI implements BottomBar.IBottomBarFFI {
  private _ffi: BottomBar.BottomBarAndroidFFI = (window as any).bottom_bar;

  async getHidden(): Promise<boolean> {
    return await netCallNative(NativeUI.GetBottomBarEnabled);
  }

  async setHidden(isEnabled: boolean = true): Promise<void> {
    return await netCallNative(NativeUI.SetBottomBarEnabled, isEnabled);
  }

  async getOverlay(): Promise<number> {
    return await netCallNative(NativeUI.GetBottomBarOverlay);
  }

  async setOverlay(alpha: string): Promise<number> {
    return await netCallNative(NativeUI.SetBottomBarOverlay, alpha);
  }

  async getHeight(): Promise<number> {
    return await netCallNative(NativeUI.GetBottomBarHeight);
  }

  async setHeight(height: number): Promise<void> {
    return await netCallNative(NativeUI.SetBottomBarHeight, height);
  }

  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const color = await netCallNative(NativeUI.GetBottomBarBackgroundColor);
    const colorHex = getColorHex(color);
    return colorHex
  }

  async setBackgroundColor(color: Color.RGBAHex): Promise<void> {
    const colorHex = getColorInt(
      color.slice(0, -2) as Color.RGBHex,
      color.slice(-2) as Color.AlphaValueHex
    );
    return await netCallNative(NativeUI.SetBottomBarBackgroundColor, colorHex);
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    const color = await netCallNative(NativeUI.GetBottomBarForegroundColor);
    const colorHex = getColorHex(color);
    return colorHex;
  }

  async setForegroundColor(color: Color.RGBAHex): Promise<void> {
    const colorHex = getColorInt(
      color.slice(0, -2) as Color.RGBHex,
      color.slice(-2) as Color.AlphaValueHex
    );
    return await netCallNative(NativeUI.SetBottomBarForegroundColor, colorHex);
  }

  async getActions(): Promise<BottomBar.BottomBarItem[]> {
    const actionList = JSON.parse(await netCallNative(NativeUI.GetBottomBarActions));
    const _actionList: BottomBar.BottomBarItem[] = [];

    for (const item of actionList) {
      if (item.colors) {
        for (let key of Object.keys(item.colors)) {
          let color = item.colors[key as keyof BottomBar.IBottomBarColors];

          if (typeof color === "number") {
            let colorARGB = "#" + color.toString(16);

            item.colors[
              key as keyof BottomBar.IBottomBarColors
            ] = (colorARGB.slice(0, 1) +
              colorARGB.slice(3) +
              colorARGB.slice(1, 3)) as BottomBar.BottomBarColorType;
          }
        }
      }

      _actionList.push(item);
    }
    return _actionList
  }

  async setActions(actionList: BottomBar.BottomBarItem[]): Promise<void> {
    let _actionList: BottomBar.BottomBarItem[] = [];

    for (const item of actionList) {
      if (item.colors) {
        for (const key of Object.keys(item.colors)) {
          let color = item.colors[key as keyof BottomBar.IBottomBarColors];

          if (typeof color === "string") {
            let colorRGBA = convertToRGBAHex(color).replace("#", "0x");
            let colorARGB =
              colorRGBA.slice(0, 2) +
              colorRGBA.slice(-2) +
              colorRGBA.slice(2, -2);
            item.colors[key as keyof BottomBar.IBottomBarColors] = parseInt(
              colorARGB
            );
          }
        }
      }

      _actionList.push(item);
    }

    return await netCallNative(NativeUI.SetBottomBarActions, _actionList);
  }
}