export class TopBarFFI implements Plaoc.ITopBarFFI {
  private _ffi: Plaoc.TopBarAndroidFFI = top_bar;
  private _alpha: number = 1;

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

  async setHidden(): Promise<void> {
    const isEnabled = await this.getEnabled();

    if (isEnabled) {
      this.toggleEnabled();
    }

    return;
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

  getActions(): Promise<Plaoc.TopBarItem[]> {
    return new Promise<Plaoc.TopBarItem[]>((resolve, reject) => {
      const actionList = JSON.parse(this._ffi.getActions());

      resolve(actionList);
    });
  }

  setActions(actionList: Plaoc.TopBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setActions(JSON.stringify(actionList));

      resolve();
    });
  }

  // private getColorInt(color: Plaoc.RGB = "#ffffff", alpha: number): number {
  //   alert("alpha: " + alpha);
  //   return parseInt(color.slice(1), 16) + ((alpha * 255) << (8 * 3));
  // }

  // private getColorInt(color: Plaoc.RGBA): number {
  //   return parseInt(color.replace("#", "0x"));
  // }

  private getColorInt(color: Plaoc.RGB, alpha: Plaoc.AlphaValueHex): number {
    return (
      parseInt(color.replace("#", "0x"), 16) + (parseInt("0x" + alpha) << 24)
    );
  }

  // private getColorHex(color: number): Plaoc.RGB {
  //   return "#" + (color - ((this._alpha * 255) << (8 * 3))).toString(16);
  // }

  private getColorHex(color: number, alpha: Plaoc.AlphaValueHex): Plaoc.RGB {
    return "#" + (color - (parseInt("0x" + alpha) << 24)).toString(16);
  }

  getBackgroundColor(alpha: Plaoc.AlphaValueHex): Promise<Plaoc.RGB> {
    return new Promise<Plaoc.RGB>((resolve, reject) => {
      const color = this._ffi.getBackgroundColor();
      alert("color: " + color);
      const colorHex = this.getColorHex(color, alpha);

      resolve(colorHex);
    });
  }

  setBackgroundColor(
    color: Plaoc.RGB,
    alpha: Plaoc.AlphaValueHex
  ): Promise<void> {
    return new Promise<void>(async (resolve, reject) => {
      const colorHex = this.getColorInt(color, alpha);

      this._ffi.setBackgroundColor(colorHex);

      resolve();
    });
  }

  getForegroundColor(alpha: Plaoc.AlphaValueHex): Promise<Plaoc.RGB> {
    return new Promise<Plaoc.RGB>((resolve, reject) => {
      const color = this._ffi.getForegroundColor();
      const colorHex = this.getColorHex(color, alpha);

      resolve(colorHex);
    });
  }

  setForegroundColor(
    color: Plaoc.RGB,
    alpha: Plaoc.AlphaValueHex
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const colorHex = this.getColorInt(color, alpha);

      this._ffi.setForegroundColor(colorHex);

      resolve();
    });
  }
}
