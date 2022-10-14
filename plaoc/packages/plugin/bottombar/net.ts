import { NativeUI } from "../common/nativeHandle.ts";
import { netCallNativeUi } from "@bfsx/gateway";
import { Color } from "../types/colorType.ts";
import { convertToRGBAHex, getColorHex, getColorInt } from "../util/index.ts";
import { BottomBar } from "./bfcsBottomBarType.ts";
export class BottomBarNet implements BottomBar.IBottomBarNet {

  async getHidden(): Promise<boolean> {
    return await netCallNativeUi(NativeUI.GetBottomBarEnabled);
  }

  async setHidden(isEnabled = true): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetBottomBarEnabled, isEnabled);
  }

  async getBottomBarAlpha(): Promise<number> {
    return await netCallNativeUi(NativeUI.GetBottomBarAlpha);
  }

  async setBottomBarAlpha(alpha: string): Promise<number> {
    return await netCallNativeUi(NativeUI.SetBottomBarAlpha, alpha);
  }

  async getHeight(): Promise<number> {
    return await netCallNativeUi(NativeUI.GetBottomBarHeight);
  }

  async setHeight(height: number): Promise<void> {
    return await netCallNativeUi(NativeUI.SetBottomBarHeight, height);
  }

  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const colorHex = await netCallNativeUi(NativeUI.GetBottomBarBackgroundColor);
    return colorHex;
  }

  async setBackgroundColor(colorHex: Color.RGBAHex): Promise<void> {
    return await netCallNativeUi(NativeUI.SetBottomBarBackgroundColor, colorHex);
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    const colorHex = await netCallNativeUi(NativeUI.GetBottomBarForegroundColor);
    return colorHex;
  }

  async setForegroundColor(colorHex: Color.RGBAHex): Promise<void> {
    return await netCallNativeUi(NativeUI.SetBottomBarForegroundColor, colorHex);
  }

  async getActions(): Promise<BottomBar.BottomBarItem[]> {
    const actionList = JSON.parse(
      await netCallNativeUi(NativeUI.GetBottomBarActions),
    );
    const _actionList: BottomBar.BottomBarItem[] = [];

    for (const item of actionList) {
      if (item.colors) {
        for (const key of Object.keys(item.colors)) {
          const color = item.colors[key as keyof BottomBar.IBottomBarColors];

          if (typeof color === "number") {
            const colorARGB = "#" + color.toString(16);

            item.colors[key as keyof BottomBar.IBottomBarColors] =
              (colorARGB.slice(0, 1) +
                colorARGB.slice(3) +
                colorARGB.slice(1, 3)) as BottomBar.BottomBarColorType;
          }
        }
      }

      _actionList.push(item);
    }
    return _actionList;
  }

  async setActions(actionList: BottomBar.BottomBarItem[]): Promise<void> {
    const _actionList: BottomBar.BottomBarItem[] = [];

    for (const item of actionList) {
      if (item.colors) {
        for (const key of Object.keys(item.colors)) {
          const color = item.colors[key as keyof BottomBar.IBottomBarColors];

          if (typeof color === "string") {
            const colorRGBA = convertToRGBAHex(color).replace("#", "0x");
            const colorARGB = colorRGBA.slice(0, 2) +
              colorRGBA.slice(-2) +
              colorRGBA.slice(2, -2);
            item.colors[key as keyof BottomBar.IBottomBarColors] = parseInt(
              colorARGB,
            );
          }
        }
      }

      _actionList.push(item);
    }

    return await netCallNativeUi(NativeUI.SetBottomBarActions, _actionList);
  }
}
