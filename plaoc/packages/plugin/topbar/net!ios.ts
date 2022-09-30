import { Color } from "../types/colorType.ts";
import { TopBar } from "./bfcsTopBarType.ts";
import { NativeUI } from "../common/nativeHandle.ts";
import { netCallNativeUi } from "@bfsx/gateway";
import { getColorHex, hexToIntColor } from "../util/index.ts";

export class TopBarFFI implements TopBar.ITopBarFFI {
  async topBarNavigationBack(): Promise<boolean> {
    return await netCallNativeUi(NativeUI.TopBarNavigationBack);
  }

  async getTopBarEnabled(): Promise<boolean> {
    const isEnabled = await netCallNativeUi(NativeUI.GetTopBarEnabled);
    return Boolean(isEnabled);
  }

  async setTopBarEnabled(isEnabled: boolean): Promise<void> {
    await netCallNativeUi(NativeUI.SetTopBarEnabled, isEnabled);
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
    const isOverlay = await netCallNativeUi(NativeUI.GetTopBarOverlay);

    return Boolean(isOverlay);
  }

  async setTopBarOverlay(alpha: string): Promise<void> {
    await netCallNativeUi(NativeUI.SetTopBarOverlay, Number(alpha));

    return;
  }

  async getTopBarTitle(): Promise<string> {
    const title = await netCallNativeUi(NativeUI.GetTopBarTitle);
    return title.toString();
  }

  async setTopBarTitle(title: string): Promise<void> {
    await netCallNativeUi(NativeUI.SetTopBarTitle, title);
    return;
  }

  async hasTopBarTitle(): Promise<boolean> {
    const has = await netCallNativeUi(NativeUI.HasTopBarTitle);
    return Boolean(has);
  }

  async getTopBarHeight(): Promise<number> {
    const height = await netCallNativeUi(NativeUI.GetTopBarHeight);

    return Number(height);
  }

  async getTopBarActions(): Promise<TopBar.TopBarItem[]> {
    const actionList = (await netCallNativeUi(
      NativeUI.GetTopBarActions,
    )) as string;

    return JSON.parse(actionList);
  }

  async setTopBarActions(actionList: TopBar.TopBarItem[]): Promise<void> {
    await netCallNativeUi(NativeUI.SetTopBarActions, actionList);
    return;
  }

  async getTopBarBackgroundColor(): Promise<Color.RGBAHex> {
    const stringColor = (await netCallNativeUi(
      NativeUI.GetTopBarBackgroundColor,
    )) as string;
    const colorHex = getColorHex(parseFloat(stringColor));

    return colorHex;
  }

  async setTopBarBackgroundColor(color: Color.RGBAHex): Promise<void> {
    const colorHex = hexToIntColor(color);
    await netCallNativeUi(NativeUI.SetTopBarBackgroundColor, colorHex);

    return;
  }

  async getTopBarForegroundColor(): Promise<Color.RGBAHex> {
    const stringColor = (await netCallNativeUi(
      NativeUI.GetTopBarForegroundColor,
    )) as string;
    const colorHex = getColorHex(parseFloat(stringColor));

    return colorHex;
  }

  async setTopBarForegroundColor(color: Color.RGBAHex): Promise<void> {
    const colorHex = hexToIntColor(color);
    await netCallNativeUi(NativeUI.SetTopBarForegroundColor, colorHex);

    return;
  }
}
