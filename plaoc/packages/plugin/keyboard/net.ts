import { getCallNativeUi } from "@bfsx/gateway";
import { Keyboard } from "./bfcsKeyboardType.ts";
import { NativeUI } from "../common/nativeHandle.ts";

export class VirtualKeyboardNet implements Keyboard.IVirtualKeyboardNet {
  async getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    const safeArea = await getCallNativeUi(NativeUI.GetKeyBoardSafeArea);
    return JSON.parse(safeArea);
  }

  async getKeyboardHeight(): Promise<number> {
    const height = await getCallNativeUi(NativeUI.GetKeyBoardHeight);
    return parseFloat(height);
  }

  async getKeyboardOverlay(): Promise<boolean> {
    const overlay = await getCallNativeUi(NativeUI.GetKeyBoardOverlay);
    return overlay;
  }

  async setKeyboardOverlay(isOver = true): Promise<boolean> {
    const overlay = await getCallNativeUi(NativeUI.SetKeyBoardOverlay, isOver);
    return overlay;
  }

  async toggleKeyboardOverlay(): Promise<boolean> {
    const overlay = await this.getKeyboardOverlay();
    if (!overlay) {
      await this.setKeyboardOverlay(true);
    }
    return overlay;
  }

  showKeyboard(): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(async () => {
        const isShow = await getCallNativeUi(NativeUI.ShowKeyBoard);
        resolve(isShow);
      }, 100);
    });
  }

  async hideKeyboard(): Promise<boolean> {
    return await getCallNativeUi(NativeUI.HideKeyBoard);
  }
}
