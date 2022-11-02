/// <reference lib="dom" />
// import { getCallNativeUi } from "@bfsx/gateway";
import { DwebPlugin } from "./dweb-plugin.ts";
import { NativeHandle, NativeUI } from "../common/nativeHandle.ts";

export class DWebMessager extends DwebPlugin {
  constructor() {
    super();
  }
}

export class Navigation extends DwebPlugin {
  constructor() {
    super();
  }
  /**隐藏系统导航栏 默认值false隐藏 */
  setNavigationBarVisible(isHide = false) {
    return getCallNativeUi(NativeUI.SetNavigationBarVisible, isHide);
  }
  /**获取系统导航栏颜色 */
  getNavigationBarVisible() {
    return getCallNativeUi(NativeUI.GetNavigationBarVisible);
  }
  /**
   * 设置导航栏颜色
   * @param color 设置颜色
   * @param darkIcons 是否更期望使用深色效果
   * @param isNavigationBarContrastEnforced 在系统背景高度透明的时候导航栏是否应该增强对比度，android仅支持：API 29+
   * @returns Promise<true>
   */
  setNavigationBarColor(
    colorHex: string,
    darkIcons = false,
    isNavigationBarContrastEnforced = true
  ) {
    return getCallNativeUi(NativeUI.SetNavigationBarColor, {
      colorHex,
      darkIcons,
      isNavigationBarContrastEnforced,
    });
  }
  /** 获取系统导航栏是否覆盖内容*/
  getNavigationBarOverlay() {
    return getCallNativeUi(NativeUI.GetNavigationBarOverlay);
  }
  /**设置系统导航栏是否覆盖内容,默认值false为不覆盖 */
  setNavigationBarOverlay(isOverlay = false) {
    return getCallNativeUi(NativeUI.SetNavigationBarOverlay, isOverlay);
  }
}

export class OpenScanner extends DwebPlugin {
  constructor() {
    super();
  }
  // 打开二维码扫码
  openQrCodeScanner(): Promise<string> {
    return this.onPolling(NativeHandle.OpenQrScanner);
  }
  // 打开条形码扫码
  openBarCodeScanner(): Promise<string> {
    return this.onPolling(NativeHandle.BarcodeScanner);
  }
}

/**
 * 服务端的用户如果想给全部的dweb-plugin发送广播，需要在evalJs调用dwebPlugin.dispatch
 * 单独给某个webComponent发送消息则使用 组件名称.dispatch，
 * 单元测试需要使用模拟函数覆盖到两者所有组件
 */
customElements.define("dweb-messager", DWebMessager);
customElements.define("dweb-navigation", Navigation);
customElements.define("dweb-scanner", OpenScanner);
