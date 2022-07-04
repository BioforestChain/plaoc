import { getColorInt, getColorHex } from "@plaoc/plugin-util";

export class TopBarFFI implements Plaoc.ITopBarFFI {
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

  getBackgroundColor(): Promise<Plaoc.RGBAHex> {
    return new Promise<Plaoc.RGBAHex>((resolve, reject) => {
      const color = this._ffi.getBackgroundColor();
      const colorHex = getColorHex(color);

      resolve(colorHex);
    });
  }

  setBackgroundColor(color: Plaoc.RGBAHex): Promise<void> {
    return new Promise<void>(async (resolve, reject) => {
      const colorHex = getColorInt(
        color.slice(0, -2) as Plaoc.RGBHex,
        color.slice(-2) as Plaoc.AlphaValueHex
      );
      this._ffi.setBackgroundColor(colorHex);

      resolve();
    });
  }

  getForegroundColor(): Promise<Plaoc.RGBAHex> {
    return new Promise<Plaoc.RGBAHex>((resolve, reject) => {
      const color = this._ffi.getForegroundColor();
      const colorHex = getColorHex(color);

      resolve(colorHex);
    });
  }

  setForegroundColor(color: Plaoc.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const colorHex = getColorInt(
        color.slice(0, -2) as Plaoc.RGBHex,
        color.slice(-2) as Plaoc.AlphaValueHex
      );

      this._ffi.setForegroundColor(colorHex);

      resolve();
    });
  }
}
