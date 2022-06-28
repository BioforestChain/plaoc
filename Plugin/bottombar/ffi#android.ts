export class BottomBarFFI {
  private _ffi: Plaoc.BottomBarAndroidFFI = bottom_bar;

  getEnabled(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const isEnabled = this._ffi.getEnabled();

      resolve(isEnabled);
    });
  }

  toggleEnabled(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleEnabled(0);
      resolve();
    });
  }

  setHidden(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleEnabled(1);

      resolve();
    });
  }

  getOverlay(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const overlay = this._ffi.getOverlay();

      resolve(overlay);
    });
  }

  toggleOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleOverlay(0);

      resolve();
    });
  }

  setOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleOverlay(1);

      resolve();
    });
  }

  getHeight(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const height = this._ffi.getHeight();

      resolve(height);
    });
  }

  setHeight(height: number): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setHeight(height);

      resolve();
    });
  }

  private getColorInt(color: string = "#ffffff", alpha: number = 0.5) {
    return parseInt(color.slice(1), 16) + ((alpha * 255) << (8 * 3));
  }

  getBackgroundColor(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const color = this._ffi.getBackgroundColor();
      const colorHex = "#" + color.toString(16).slice(2);

      resolve(colorHex);
    });
  }

  setBackgroundColor(colorObject: Plaoc.IColor): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const colorHex = this.getColorInt(colorObject.color, colorObject.alpha);
      this._ffi.setBackgroundColor(colorHex);

      resolve();
    });
  }

  getForegroundColor(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const color = this._ffi.getForegroundColor();
      const colorHex = "#" + color.toString(16).slice(2);

      resolve(colorHex);
    });
  }

  setForegroundColor(colorObject: Plaoc.IColor): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const colorHex = this.getColorInt(colorObject.color, colorObject.alpha);
      this._ffi.setForegroundColor(colorHex);

      resolve();
    });
  }

  getActions(): Promise<Plaoc.BottomBarItem[]> {
    return new Promise<Plaoc.BottomBarItem[]>((resolve, reject) => {
      const actionList = JSON.parse(this._ffi.getActions());

      resolve(actionList);
    });
  }

  setActions(actionList: Plaoc.BottomBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setActions(JSON.stringify(actionList));

      resolve();
    });
  }
}
