import { NativeUI } from "../common/nativeHandle.ts";
import { netCallNativeUi } from "@bfsx/gateway";
import { Color } from "../types/colorType.ts";
import { convertToRGBAHex } from "../util/index.ts";
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

  async setHeight(height: number): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetBottomBarHeight, height);
  }

  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const colorHex = await netCallNativeUi(NativeUI.GetBottomBarBackgroundColor);
    return colorHex;
  }

  async setBackgroundColor(colorHex: Color.RGBAHex): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetBottomBarBackgroundColor, colorHex);
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    const colorHex = await netCallNativeUi(NativeUI.GetBottomBarForegroundColor);
    return colorHex;
  }

  async setForegroundColor(colorHex: Color.RGBAHex): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetBottomBarForegroundColor, colorHex);
  }

  async getActions(): Promise<BottomBar.BottomBarItem[]> {
    const actionList = JSON.parse(
      await netCallNativeUi(NativeUI.GetBottomBarActions),
    );
    return actionList;
  }

  async setActions(actionList: BottomBar.BottomBarItem[]): Promise<void> {
    return await netCallNativeUi(NativeUI.SetBottomBarActions, actionList);
  }
}
