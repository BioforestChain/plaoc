export class TopBarFFI {
  private _ffi: Plaoc.TopBarAndroidFFI = top_bar;

  back(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.back();
      resolve();
    });
  }

  getEnabled(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const isEnabled = this._ffi.getEnabled();
      resolve(isEnabled);
    });
  }

  toggleEnabled() {
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
      const isOverlay = this._ffi.getOverlay();

      resolve(isOverlay);
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

  getTitle(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const title = this._ffi.getTitle();
      resolve(title);
    });
  }

  setTitle(title: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setTitle(title);
      resolve();
    });
  }

  hasTitle(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const hasTitle = this._ffi.hasTitle();

      resolve(hasTitle);
    });
  }

  getHeight(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const height = this._ffi.getHeight();

      resolve(height);
    });
  }

  async getActions(): Promise<Plaoc.TopBarItem[]> {
    const actionList = JSON.parse(this._ffi.getActions());

    return actionList;
  }

  setActions(actionList: Plaoc.TopBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setActions(JSON.stringify(actionList));

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
}
