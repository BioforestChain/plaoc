import "../typings";

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

  // ios端ffi
  export interface StatusBarIosFFI {
    updateStatusBackgroundColor: {
      postMessage(colorHex: Color.RGBAHex): void;
    };
    statusBackgroundColor: {
      postMessage(noValue: null): Promise<Color.RGBAHex>;
    };
    getStatusBarVisible: {
      postMessage(noValue: null): Promise<boolean>;
    };
    updateStatusHidden: {
      postMessage(isHidden: string): void;
    };
    getStatusBarOverlay: {
      postMessage(noValue: null): Promise<boolean>;
    };
    updateStatusBarOverlay: {
      postMessage(overlay: number): void;
    };
    statusBarStyle: {
      postMessage(noValue: null): StatusBarIosStyle;
    };
    updateStatusStyle: {
      postMessage(state: StatusBarIosStyle): void;
    };
  }

  // desktop-dev端ffi
  export interface StatusBarDesktopFFI {
    setStatusbarBackgroundColor(
      scopedValue: Color.ColorFormatType | undefined,
      globalValue?: Color.ColorFormatType
    ): Promise<void>;
    getStatusbarBackgroundColor(): Promise<Color.ColorFormatType>;
    setStatusbarOverlay(
      scopedValue: boolean | undefined,
      globalValue?: boolean
    ): Promise<void>;
    getStatusbarOverlay(): Promise<boolean>;
    setStatusbarHidden(
      scopedValue: boolean | undefined,
      globalValue?: boolean
    ): Promise<void>;
    getStatusbarHidden(): Promise<boolean>;
    setStatusbarStyle(
      scopedValue: StatusBarStyle | undefined,
      globalValue?: StatusBarStyle
    ): Promise<void>;
    getStatusbarStyle(): Promise<StatusBarStyle>;
  }

  // 三端最后统一封装ffi
  export interface IStatusBarFFI {
    setStatusBarColor(
      colorHex?: string,
      barStyle?: StatusBarStyle
    ): Promise<void>;
    getStatusBarColor(): Promise<Color.RGBAHex>;
    getStatusBarVisible(): Promise<boolean>;
    toggleStatusBarVisible(): Promise<void>;
    setStatusBarHidden(): Promise<void>;
    getStatusBarOverlay(): Promise<boolean>;
    toggleStatusBarOverlay(): Promise<void>;
    setStatusBarOverlay(): Promise<void>;
    getStatusBarStyle(): Promise<StatusBarStyle>;
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
