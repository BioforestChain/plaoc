import { NativeUI } from "../common/nativeHandle";
import { netCallNative } from "../common/network";
import "../typings";
import { Color } from "../typings/types/color.type";
import { getColorInt, getColorHex, hexToIntColor } from "../util";
import { TopBar } from "./bfcsTopBar.type";

// 禁止传递float 因为不管传递什么，到android都会变0。
export class TopBarFFI implements TopBar.ITopBarFFI {
  async topBarNavigationBack(): Promise<void> {
    await netCallNative(NativeUI.TopBarNavigationBack);

    return;
  }

  async getTopBarEnabled(): Promise<boolean> {
    const isEnabled = await netCallNative(NativeUI.GetTopBarEnabled);

    return Boolean(isEnabled);
  }

  async setTopBarEnabled(isEnabled: boolean): Promise<void> {
    await netCallNative(NativeUI.SetTopBarEnabled, isEnabled);

    return;
  }

  async setTopBarHidden(): Promise<void> {
    const isEnabled = await this.getTopBarEnabled();

    if (isEnabled) {
      await this.setTopBarEnabled(false);
    }

    return;
  }

  async getTopBarOverlay(): Promise<boolean> {
    const isOverlay = await netCallNative(NativeUI.GetTopBarOverlay);

    return Boolean(isOverlay);
  }

  async setTopBarOverlay(alpha: string): Promise<void> {
    await netCallNative(NativeUI.SetTopBarOverlay, alpha);

    return;
  }

  async getTopBarTitle(): Promise<string> {
    const title = await netCallNative(NativeUI.GetTopBarTitle);

    return title.toString();
  }

  async setTopBarTitle(title: string): Promise<void> {
    await netCallNative(NativeUI.SetTopBarTitle, title);

    return;
  }

  async hasTopBarTitle(): Promise<boolean> {
    const has = await netCallNative(NativeUI.HasTopBarTitle);

    return Boolean(has);
  }

  async getTopBarHeight(): Promise<number> {
    const height = await netCallNative(NativeUI.GetTopBarHeight);

    return Number(height);
  }

  async getTopBarActions(): Promise<TopBar.TopBarItem[]> {
    const actionList = (await netCallNative(
      NativeUI.GetTopBarActions
    )) as string;

    return JSON.parse(actionList);
  }

  async setTopBarActions(actionList: TopBar.TopBarItem[]): Promise<void> {
    await netCallNative(NativeUI.SetTopBarActions, JSON.stringify(actionList));

    return;
  }

  async getTopBarBackgroundColor(): Promise<Color.RGBAHex> {
    const stringColor = (await netCallNative(
      NativeUI.GetTopBarBackgroundColor
    )) as string;
    const colorHex = getColorHex(parseFloat(stringColor));

    return colorHex;
  }

  async setTopBarBackgroundColor(color: Color.RGBAHex): Promise<void> {
    const colorHex = hexToIntColor(color);
    await netCallNative(NativeUI.SetTopBarBackgroundColor, colorHex);

    return;
  }

  async getTopBarForegroundColor(): Promise<Color.RGBAHex> {
    const stringColor = (await netCallNative(
      NativeUI.GetTopBarForegroundColor
    )) as string;
    const colorHex = getColorHex(parseFloat(stringColor));

    return colorHex;
  }

  async setTopBarForegroundColor(color: Color.RGBAHex): Promise<void> {
    const colorHex = hexToIntColor(color);
    await netCallNative(NativeUI.SetTopBarForegroundColor, colorHex);

    return;
  }
}
