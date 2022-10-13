import { Color } from "../types/colorType.ts";
import { TopBar } from "./bfcsTopBarType.ts";
import { NativeUI } from "../common/nativeHandle.ts";
import { netCallNativeUi } from "@bfsx/gateway";

export class TopBarFFI implements TopBar.ITopBarNet {
  async topBarNavigationBack(): Promise<boolean> {
    return await netCallNativeUi(NativeUI.TopBarNavigationBack);
  }

  async getTopBarShow(): Promise<boolean> {
    const isEnabled = await netCallNativeUi(NativeUI.GetTopBarShow);
    return Boolean(isEnabled);
  }

  async setTopBarShow(isEnabled: boolean): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetTopBarShow, isEnabled);
  }

  async setTopBarHidden(): Promise<boolean> {
    const isEnabled = await this.getTopBarShow();

    if (isEnabled) {
      await this.setTopBarShow(false);
    }

    return isEnabled;
  }

  async getTopBarOverlay(): Promise<boolean> {
    const isOverlay = await netCallNativeUi(NativeUI.GetTopBarOverlay);

    return Boolean(isOverlay);
  }

  async setTopBarOverlay(alpha: string): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetTopBarOverlay, Number(alpha));
  }

  async getTopBarAlpha(): Promise<number> {
    const alpha = await netCallNativeUi(NativeUI.GetTopBarAlpha);

    return alpha;
  }

  async setTopBarAlpha(alpha: string): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetTopBarAlpha, Number(alpha));
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
    return await netCallNativeUi(NativeUI.GetTopBarBackgroundColor)
  }

  async setTopBarBackgroundColor(color: Color.RGBAHex): Promise<void> {
    return await netCallNativeUi(NativeUI.SetTopBarBackgroundColor, color);
  }

  async getTopBarForegroundColor(): Promise<Color.RGBAHex> {
    return await netCallNativeUi(NativeUI.GetTopBarForegroundColor)
  }

  async setTopBarForegroundColor(color: Color.RGBAHex): Promise<void> {
    return await netCallNativeUi(NativeUI.SetTopBarForegroundColor, color);
  }
}
