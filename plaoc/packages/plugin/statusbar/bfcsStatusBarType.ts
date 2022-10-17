import { Color } from "../types/colorType.ts";

export namespace StatusBar {
  // android端ffi
  export interface StatusBarAndroidFFI {
    setStatusBarColor(colorHex: number, darkIcons: StatusBarAndroidStyle): void;
    getStatusBarColor(): number;
    getStatusBarVisible(): boolean;
    toggleStatusBarVisible(visible: number): void;
    getStatusBarOverlay(): boolean;
    toggleStatusBarOverlay(isOverlay: number): void;
    getStatusBarStyle(): boolean;
  }


  // desktop-dev端ffi
  export interface StatusBarDesktopFFI {
    getStatusBarIsDark(): unknown;
    setStatusbarBackgroundColor(
      scopedValue: Color.ColorFormatType | undefined,
      globalValue?: Color.ColorFormatType,
    ): Promise<void>;
    getStatusbarBackgroundColor(): Promise<Color.ColorFormatType>;
    setStatusbarOverlay(
      scopedValue: boolean | undefined,
      globalValue?: boolean,
    ): Promise<boolean>;
    getStatusbarOverlay(): Promise<boolean>;
    setStatusbarHidden(
      scopedValue: boolean | undefined,
      globalValue?: boolean,
    ): Promise<boolean>;
    getStatusbarHidden(): Promise<boolean>;
    setStatusbarStyle(
      scopedValue: StatusBarStyle | undefined,
      globalValue?: StatusBarStyle,
    ): Promise<void>;
    getStatusbarStyle(): Promise<StatusBarStyle>;
  }

  export interface IStatusBarNet {
    setStatusBarColor(
      colorHex?: string,
      barStyle?: StatusBarStyle,
    ): Promise<void>;
    getStatusBarColor(): Promise<Color.RGBAHex>;
    getStatusBarVisible(): Promise<boolean>;
    setStatusBarVisible(isVer: boolean): Promise<boolean>;
    setStatusBarHidden(): Promise<boolean>;
    getStatusBarOverlay(): Promise<boolean>;
    setStatusBarOverlay(isOver: boolean): Promise<boolean>;
    getStatusBarIsDark(): Promise<StatusBarStyle>;
  }

  // default:	默认的样式（IOS 为白底黑字、Android 为黑底白字、Desktop-dev同Android）
  // light-content:	黑底白字
  // dark-content:	白底黑字（需要 Android API>=23）
  export enum StatusBarStyle {
    DEFAULT = "default",
    LIGHT_CONTENT = "light-content",
    DARK_CONTENT = "dark-content",
  }

  export enum StatusBarIosStyle {
    DEFAULT = "default",
    LIGHT_CONTENT = "lightContent",
    DARK_CONTENT = "default",
  }

  export enum StatusBarAndroidStyle {
    DEFAULT = 0,
    LIGHT_CONTENT = -1,
    DARK_CONTENT = 1,
  }
}
