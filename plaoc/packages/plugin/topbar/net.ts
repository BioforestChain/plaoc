import { NativeUI } from "../common/nativeHandle.ts";
import { getCallNativeUi, postCallNativeUi } from "@bfsx/gateway";
import { Color } from "../types/colorType.ts";
import { TopBar } from "./bfcsTopBarType.ts";

export class TopBarNet implements TopBar.ITopBarNet {
  async topBarNavigationBack(): Promise<boolean> {
    return await getCallNativeUi(NativeUI.TopBarNavigationBack);
  }

  async getTopBarShow(): Promise<boolean> {
    const isShow = await getCallNativeUi(NativeUI.GetTopBarShow);
    return Boolean(isShow);
  }

  async setTopBarShow(isShow: boolean): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetTopBarShow, isShow);
  }

  async setTopBarHidden(): Promise<boolean> {
    const isShow = await this.getTopBarShow();
    if (isShow) {
      await this.setTopBarShow(false);
    }
    return isShow;
  }

  async getTopBarOverlay(): Promise<boolean> {
    const isOverlay = await getCallNativeUi(NativeUI.GetTopBarOverlay);

    return Boolean(isOverlay);
  }

  async setTopBarOverlay(alpha: string): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetTopBarOverlay, Number(alpha));
  }


  async getTopBarAlpha(): Promise<number> {
    const alpha = await getCallNativeUi(NativeUI.GetTopBarAlpha);

    return alpha;
  }

  async setTopBarAlpha(alpha: string): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetTopBarAlpha, Number(alpha));
  }

  async getTopBarTitle(): Promise<string> {
    const title = await getCallNativeUi(NativeUI.GetTopBarTitle);
    return title.toString();
  }

  async setTopBarTitle(title: string): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetTopBarTitle, title);
  }

  async hasTopBarTitle(): Promise<boolean> {
    const has = await getCallNativeUi(NativeUI.HasTopBarTitle);
    return Boolean(has);
  }

  async getTopBarHeight(): Promise<number> {
    const height = await getCallNativeUi(NativeUI.GetTopBarHeight);
    return Number(height);
  }

  async getTopBarActions(): Promise<TopBar.TopBarItem[]> {
    const actionList = (await getCallNativeUi(
      NativeUI.GetTopBarActions,
    )) as string;

    return JSON.parse(actionList);
  }

  async setTopBarActions(actionList: TopBar.TopBarItem[]): Promise<void> {
    await postCallNativeUi(NativeUI.SetTopBarActions, actionList);
    return;
  }


  async getTopBarBackgroundColor(): Promise<Color.RGBAHex> {
    return await getCallNativeUi(NativeUI.GetTopBarBackgroundColor)
  }

  async setTopBarBackgroundColor(color: Color.RGBAHex): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetTopBarBackgroundColor, color);
  }

  async getTopBarForegroundColor(): Promise<Color.RGBAHex> {
    return await getCallNativeUi(NativeUI.GetTopBarForegroundColor)
  }

  async setTopBarForegroundColor(color: Color.RGBAHex): Promise<boolean> {
    return await getCallNativeUi(NativeUI.SetTopBarForegroundColor, color);
  }


}
