declare namespace Plaoc {
  // android端ffi
  interface StatusBarAndroidFFI {
    setStatusBarColor(colorHex: number, darkIcons: StatusBarAndroidStyle): void;
    getStatusBarColor(): number;
    getStatusBarVisible(): boolean;
    toggleStatusBarVisible(visible: number): void;
    getStatusBarOverlay(): boolean;
    toggleStatusBarOverlay(isOverlay: number): void;
    getStatusBarStyle(): boolean;
  }

  // ios端ffi
  interface StatusBarIosFFI {
    updateStatusBackgroundColor: {
      postMessage(colorHex: Plaoc.RGBAHex): void;
    };
    statusBackgroundColor: {
      postMessage(noValue: null): Promise<Plaoc.RGBAHex>;
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
  interface StatusBarDesktopFFI {
    setStatusBarColor(colorHex: string, darkIcons: StatusBarAndroidStyle): void;
    getStatusBarColor(): string;
    getStatusBarVisible(): boolean;
    toggleStatusBarVisible(visible: boolean): void;
    getStatusBarOverlay(): boolean;
    toggleStatusBarOverlay(isOverlay: boolean): void;
    getStatusBarStyle(): boolean;
  }

  // 三端最后统一封装ffi
  interface IStatusBarFFI {
    setStatusBarColor(
      colorHex?: string,
      barStyle?: StatusBarStyle
    ): Promise<void>;
    getStatusBarColor(): Promise<Plaoc.RGBAHex>;
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
  enum StatusBarStyle {
    DEFAULT = "default",
    LIGHT_CONTENT = "light-content",
    DARK_CONTENT = "dark-content",
  }

  enum StatusBarIosStyle {
    DEFAULT = "default",
    LIGHT_CONTENT = "lightContent",
    DARK_CONTENT = "default",
  }

  enum StatusBarAndroidStyle {
    DEFAULT = 0,
    LIGHT_CONTENT = -1,
    DARK_CONTENT = 1,
  }
}

declare const system_ui: Plaoc.StatusBarAndroidFFI;
