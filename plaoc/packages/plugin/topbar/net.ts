import { NativeUI } from "../common/nativeHandle.ts";
import { netCallNativeUi } from "@bfsx/gateway";
import { Color } from "../types/colorType.ts";
import { convertToRGBAHex } from "../util/index.ts";
import { TopBar } from "./bfcsTopBarType.ts";

export class TopBarNet implements TopBar.ITopBarNet {
  async topBarNavigationBack(): Promise<boolean> {
    return await netCallNativeUi(NativeUI.TopBarNavigationBack);
  }

  async getTopBarShow(): Promise<boolean> {
    const isShow = await netCallNativeUi(NativeUI.GetTopBarShow);
    return Boolean(isShow);
  }

  async setTopBarShow(isShow: boolean): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetTopBarShow, isShow);
  }

  async setTopBarHidden(): Promise<boolean> {
    const isShow = await this.getTopBarShow();
    if (isShow) {
      await this.setTopBarShow(false);
    }
    return isShow;
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

  async setTopBarTitle(title: string): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetTopBarTitle, title);
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

  async setTopBarBackgroundColor(color: Color.RGBAHex): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetTopBarBackgroundColor, convertToRGBAHex(color));
  }

  async getTopBarForegroundColor(): Promise<Color.RGBAHex> {
    return await netCallNativeUi(NativeUI.GetTopBarForegroundColor)
  }

  async setTopBarForegroundColor(color: Color.RGBAHex): Promise<boolean> {
    return await netCallNativeUi(NativeUI.SetTopBarForegroundColor, convertToRGBAHex(color));
  }


}
