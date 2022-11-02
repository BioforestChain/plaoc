import { NativeUI } from "../common/nativeHandle.ts";
// import { getCallNativeUi } from "@bfsx/gateway";
import { Color } from "../types/colorType.ts";
import { BottomBar } from "./bfcsBottomBarType.ts";
export class BottomBarNet implements BottomBar.IBottomBarNet {
  async getHidden(): Promise<boolean> {
    return await getCallNativeUi(NativeUI.GetBottomBarEnabled);
  }

  async setHidden(isEnabled = true): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetBottomBarEnabled, isEnabled);
  }

  async getBottomBarAlpha(): Promise<number> {
    return await getCallNativeUi(NativeUI.GetBottomBarAlpha);
  }

  async setBottomBarAlpha(alpha: string): Promise<number> {
    return await getCallNativeUi(NativeUI.SetBottomBarAlpha, alpha);
  }

  async getHeight(): Promise<number> {
    return await getCallNativeUi(NativeUI.GetBottomBarHeight);
  }

  async setHeight(height: number): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetBottomBarHeight, height);
  }

  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const colorHex = await getCallNativeUi(
      NativeUI.GetBottomBarBackgroundColor
    );
    return colorHex;
  }

  async setBackgroundColor(colorHex: Color.RGBAHex): Promise<boolean> {
    return await getCallNativeUi(
      NativeUI.SetBottomBarBackgroundColor,
      colorHex
    );
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    const colorHex = await getCallNativeUi(
      NativeUI.GetBottomBarForegroundColor
    );
    return colorHex;
  }

  async setForegroundColor(colorHex: Color.RGBAHex): Promise<boolean> {
    return await getCallNativeUi(
      NativeUI.SetBottomBarForegroundColor,
      colorHex
    );
  }

  async getActions(): Promise<BottomBar.BottomBarItem[]> {
    const actionList = JSON.parse(
      await getCallNativeUi(NativeUI.GetBottomBarActions)
    );
    return actionList;
  }

  async setActions(actionList: BottomBar.BottomBarItem[]): Promise<void> {
    return await getCallNativeUi(NativeUI.SetBottomBarActions, actionList);
  }
}
