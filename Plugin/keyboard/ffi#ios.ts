export class VirtualKeyboardFFI implements Plaoc.IVirtualKeyboardFFI {
  private _ffi = (window as any).webkit
    .messageHandlers as Plaoc.VirtualKeyboardIosFFI;

  async getKeyboardSafeArea(): Promise<Plaoc.IKeyboardSafeArea> {
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

  setKeyboardOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setKeyboardOverlay.postMessage("1");

      resolve();
    });
  }

  showKeyboard(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      return;
    });
  }

  hideKeyboard(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.hideKeyboard.postMessage(null);

      resolve();
    });
  }
}
