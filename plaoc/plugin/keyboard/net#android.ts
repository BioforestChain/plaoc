import { netCallNative } from "../common/network";
import { Keyboard } from "./bfcsKeyboardType";
import { NativeUI } from "../common/nativeHandle";

export class VirtualKeyboardFFI implements Keyboard.IVirtualKeyboardFFI {
  async getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    const safeArea = await netCallNative(NativeUI.GetKeyBoardSafeArea);
    return JSON.parse(safeArea);
  }

  async getKeyboardHeight(): Promise<number> {
    const height = await netCallNative(NativeUI.GetKeyBoardHeight);
    return parseFloat(height);
  }

  async getKeyboardOverlay(): Promise<boolean> {
    const overlay = await netCallNative(NativeUI.GetKeyBoardOverlay);
    return overlay;
  }

  async toggleKeyboardOverlay(isOver: Boolean = true): Promise<void> {
    const overlay = await netCallNative(NativeUI.SetKeyBoardOverlay, isOver);
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
    return new Promise(async (resolve, reject) => {
      setTimeout(async () => {
        const isShow = await netCallNative(NativeUI.ShowKeyBoard);
        resolve(isShow);
      }, 100);
    });
  }

  async hideKeyboard(): Promise<boolean> {
    return await netCallNative(NativeUI.HideKeyBoard);
  }
}
