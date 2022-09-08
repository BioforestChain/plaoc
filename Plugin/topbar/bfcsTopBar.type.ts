declare namespace Plaoc {
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
      postMessage(noValue: null): Promise<boolean>;
    };
    updateNaviBarAlpha: {
      postMessage(alpha: string): void;
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
      postMessage(noValue: null): Promise<TopBarItem[]>;
    };
    customNaviActions: {
      postMessage(actionList: TopBarItem[]): void;
    };
    getNaviBackgroundColor: {
      postMessage(noValue: null): Promise<Plaoc.IColor>;
    };
    updateNaviBarBackgroundColor: {
      postMessage(colorObject: Plaoc.IColor): void;
    };
    getNaviForegroundColor: {
      postMessage(noValue: null): Promise<Plaoc.IColor>;
    };
    updateNaviBarTintColor: {
      postMessage(colorObject: Plaoc.IColor): void;
    };
  }

  interface TopBarItem {
    icon: Plaoc.IPlaocIcon;
    onClickCode: string;
    disabled?: boolean;
  }
}

declare const top_bar: Plaoc.TopBarAndroidFFI;
// declare const top_bar_ios:
