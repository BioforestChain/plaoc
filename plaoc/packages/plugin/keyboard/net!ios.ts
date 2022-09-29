import { Keyboard } from "./bfcsKeyboardType.ts";

export class VirtualKeyboardFFI implements Keyboard.IVirtualKeyboardFFI {
  // deno-lint-ignore no-explicit-any
  private _ffi = (window as any).webkit
    .messageHandlers as Keyboard.VirtualKeyboardIosFFI;

  async getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    const safeArea = await this._ffi.keyboardSafeArea.postMessage(null);

    return safeArea;
  }

  async getKeyboardHeight(): Promise<number> {
    const height = await this._ffi.keyHeight.postMessage(null);

    return height;
  }

  async getKeyboardOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getKeyboardOverlay.postMessage(null);

    return overlay;
  }

  async toggleKeyboardOverlay(): Promise<void> {
    const overlay = await this.getKeyboardOverlay();

    this._ffi.setKeyboardOverlay.postMessage(overlay ? "0" : "1");

    return;
  }

  setKeyboardOverlay(): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this._ffi.setKeyboardOverlay.postMessage("1");

      resolve(true);
    });
  }

  showKeyboard(): Promise<boolean> {
    return new Promise(() => {
      return;
    });
  }

  hideKeyboard(): Promise<boolean> {
    return new Promise((resolve) => {
      this._ffi.hideKeyboard.postMessage(null);

      resolve(true);
    });
  }
}
