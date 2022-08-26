import { Keyboard } from "./bfcsKeyboard.type";

export class VirtualKeyboardFFI implements Keyboard.IVirtualKeyboardFFI {
  private _ffi: Keyboard.VirtualKeyboardAndroidFFI = (window as any)
    .virtual_keyboard;

  getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    return new Promise<Keyboard.IKeyboardSafeArea>((resolve, reject) => {
      const safeArea = JSON.parse(this._ffi.getSafeArea());

      resolve(safeArea);
    });
  }

  getKeyboardHeight(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const height = this._ffi.getHeight();

      resolve(height);
    });
  }

  getKeyboardOverlay(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const overlay = this._ffi.getOverlay();

      resolve(overlay);
    });
  }

  toggleKeyboardOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleOverlay(0);

      resolve();
    });
  }

  async setKeyboardOverlay(): Promise<void> {
    const overlay = await this.getKeyboardOverlay();

    if (!overlay) {
      await this.toggleKeyboardOverlay();
    }

    return;
  }

  showKeyboard(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      setTimeout(() => {
        this._ffi.show();
      }, 500);

      resolve();
    });
  }

  hideKeyboard(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.hide();

      resolve();
    });
  }
}
