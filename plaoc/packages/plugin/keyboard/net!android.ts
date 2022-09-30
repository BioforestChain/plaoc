import { netCallNativeUi } from "@bfsx/gateway";
import { Keyboard } from "./bfcsKeyboardType.ts";
import { NativeUI } from "../common/nativeHandle.ts";

export class VirtualKeyboardFFI implements Keyboard.IVirtualKeyboardFFI {
  async getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    const safeArea = await netCallNativeUi(NativeUI.GetKeyBoardSafeArea);
    return JSON.parse(safeArea);
  }

  async getKeyboardHeight(): Promise<number> {
    const height = await netCallNativeUi(NativeUI.GetKeyBoardHeight);
    return parseFloat(height);
  }

  async getKeyboardOverlay(): Promise<boolean> {
    const overlay = await netCallNativeUi(NativeUI.GetKeyBoardOverlay);
    return overlay;
  }

  async toggleKeyboardOverlay(isOver = true): Promise<void> {
    const overlay = await netCallNativeUi(NativeUI.SetKeyBoardOverlay, isOver);
    return overlay;
  }

  async setKeyboardOverlay(): Promise<boolean> {
    const overlay = await this.getKeyboardOverlay();
    if (!overlay) {
      await this.toggleKeyboardOverlay(true);
    }
    return overlay;
  }

  showKeyboard(): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(async () => {
        const isShow = await netCallNativeUi(NativeUI.ShowKeyBoard);
        resolve(isShow);
      }, 100);
    });
  }

  async hideKeyboard(): Promise<boolean> {
    return await netCallNativeUi(NativeUI.HideKeyBoard);
  }
}
