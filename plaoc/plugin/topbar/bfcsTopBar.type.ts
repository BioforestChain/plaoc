declare namespace Plaoc {
  interface TopBarItem {
    icon: Plaoc.IPlaocIcon;
    onClickCode: string;
    disabled?: boolean;
  }

  interface TopBarAndroidFFI {
    back(): void;
    getEnabled(): boolean;
    toggleEnabled(isEnabled: number): void;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    getTitle(): string;
    setTitle(title: string): void;
    hasTitle(): boolean;
    getHeight(): number;
    getActions(): Plaoc.DataString<TopBarItem[]>;
    setActions(actionList: Plaoc.DataString<TopBarItem[]>): void;
    getBackgroundColor(): number;
    setBackgroundColor(color: number): void;
    getForegroundColor(): number;
    setForegroundColor(color: number): void;
  }

  interface TopBarIosFFI {
    back: {
      postMessage(noValue: null): void;
    };
    getNaviEnabled: {
      postMessage(noValue: null): Promise<boolean>;
    };
    hiddenNaviBar: {
      postMessage(hidden: string): void;
    };
    getNaviOverlay: {
      postMessage(noValue: null): Promise<number>;
    };
    updateNaviBarOverlay: {
      postMessage(isOverlay: number): void;
    };
    getNaviTitle: {
      postMessage(noValue: null): Promise<string>;
    };
    updateTitle: {
      postMessage(title: string): void;
    };
    hasNaviTitle: {
      postMessage(noValue: null): Promise<boolean>;
    };
    naviHeight: {
      postMessage(noValue: null): Promise<number>;
    };
    getNaviActions: {
      postMessage(noValue: null): Promise<Plaoc.DataString<TopBarItem[]>>;
    };
    customNaviActions: {
      postMessage(actionList: Plaoc.DataString<TopBarItem[]>): void;
    };
    getNaviBackgroundColor: {
      postMessage(noValue: null): Promise<Plaoc.RGBAHex>;
    };
    updateNaviBarBackgroundColor: {
      postMessage(colorHex: Plaoc.RGBAHex): void;
    };
    getNaviForegroundColor: {
      postMessage(noValue: null): Promise<Plaoc.RGBAHex>;
    };
    updateNaviBarTintColor: {
      postMessage(colorHex: Plaoc.RGBAHex): void;
    };
  }

  interface ITopBarFFI {
    back(): Promise<void>;
    getEnabled(): Promise<boolean>;
    toggleEnabled(): Promise<void>;
    setHidden(): Promise<void>;
    getOverlay(): Promise<boolean>;
    toggleOverlay(): Promise<void>;
    setOverlay(): Promise<void>;
    getTitle(): Promise<string>;
    setTitle(title: string): Promise<void>;
    hasTitle(): Promise<boolean>;
    getHeight(): Promise<number>;
    getActions(): Promise<TopBarItem[]>;
    setActions(actionList: TopBarItem[]): Promise<void>;
    getBackgroundColor(): Promise<Plaoc.RGBAHex>;
    setBackgroundColor(color: Plaoc.RGBAHex): Promise<void>;
    getForegroundColor(): Promise<Plaoc.RGBAHex>;
    setForegroundColor(color: Plaoc.RGBAHex): Promise<void>;
  }
}

declare const top_bar: Plaoc.TopBarAndroidFFI;
