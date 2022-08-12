import { getColorInt, getColorHex, convertToRGBAHex } from "./../util";
import { BottomBar } from "./bfcsBottomBar.type";
const bottom_bar: any = "";
export class BottomBarFFI implements BottomBar.IBottomBarFFI {
  private _ffi: BottomBar.BottomBarAndroidFFI = bottom_bar;

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

  getBackgroundColor(): Promise<Color.RGBAHex> {
    return new Promise<Color.RGBAHex>((resolve, reject) => {
      const color = this._ffi.getBackgroundColor();
      const colorHex = getColorHex(color);

      resolve(colorHex);
    });
  }

  setBackgroundColor(color: Color.RGBAHex): Promise<void> {
    return new Promise<void>(async (resolve, reject) => {
      const colorHex = getColorInt(
        color.slice(0, -2) as Color.RGBHex,
        color.slice(-2) as Color.AlphaValueHex
      );
      this._ffi.setBackgroundColor(colorHex);

      resolve();
    });
  }

  getForegroundColor(): Promise<Color.RGBAHex> {
    return new Promise<Color.RGBAHex>((resolve, reject) => {
      const color = this._ffi.getForegroundColor();
      const colorHex = getColorHex(color);

      resolve(colorHex);
    });
  }

  setForegroundColor(color: Color.RGBAHex): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const colorHex = getColorInt(
        color.slice(0, -2) as Color.RGBHex,
        color.slice(-2) as Color.AlphaValueHex
      );

      this._ffi.setForegroundColor(colorHex);

      resolve();
    });
  }

  getActions(): Promise<BottomBar.BottomBarItem[]> {
    return new Promise<BottomBar.BottomBarItem[]>((resolve, reject) => {
      const actionList = JSON.parse(this._ffi.getActions());
      const _actionList: BottomBar.BottomBarItem[] = [];

      for (const item of actionList) {
        if (item.colors) {
          for (let key of Object.keys(item.colors)) {
            let color = item.colors[key as keyof BottomBar.IBottomBarColors];

            if (typeof color === "number") {
              let colorARGB = "#" + color.toString(16);

              item.colors[
                key as keyof BottomBar.IBottomBarColors
              ] = (colorARGB.slice(0, 1) +
                colorARGB.slice(3) +
                colorARGB.slice(1, 3)) as BottomBar.BottomBarColorType;
            }
          }
        }

        _actionList.push(item);
      }

      resolve(_actionList);
    });
  }

  setActions(actionList: BottomBar.BottomBarItem[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let _actionList: BottomBar.BottomBarItem[] = [];

      for (const item of actionList) {
        if (item.colors) {
          for (const key of Object.keys(item.colors)) {
            let color = item.colors[key as keyof BottomBar.IBottomBarColors];

            if (typeof color === "string") {
              let colorRGBA = convertToRGBAHex(color).replace("#", "0x");
              let colorARGB =
                colorRGBA.slice(0, 2) +
                colorRGBA.slice(-2) +
                colorRGBA.slice(2, -2);
              item.colors[key as keyof BottomBar.IBottomBarColors] = parseInt(
                colorARGB
              );
            }
          }
        }

        _actionList.push(item);
      }

      this._ffi.setActions(JSON.stringify(_actionList));

      resolve();
    });
  }
}
