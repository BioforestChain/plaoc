import { NativeUI } from "../common/nativeHandle.ts";
import { netCallNativeUi } from "@bfsx/gateway";
import { Color } from "../types/colorType.ts";
import { getColorHex, hexToIntColor } from "../util/index.ts";
import { TopBar } from "./bfcsTopBarType.ts";

// 禁止传递float 因为不管传递什么，到android都会变0。
export class TopBarFFI implements TopBar.ITopBarNet {
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
