import { Icon } from "../icon/bfspIcon.type";
import { Color } from "../typings/types/color.type";
import { Data } from "../typings/types/data.type";

export namespace TopBar {
  export interface TopBarItem {
    icon: Icon.IPlaocIcon;
    onClickCode: string;
    disabled?: boolean;
  }

  export interface TopBarAndroidFFI {
    back(): void;
    getEnabled(): boolean;
    toggleEnabled(isEnabled: number): void;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    getTitle(): string;
    setTitle(title: string): void;
    hasTitle(): boolean;
    getHeight(): number;
    getActions(): Data.DataString<TopBarItem[]>;
    setActions(actionList: Data.DataString<TopBarItem[]>): void;
    getBackgroundColor(): number;
    setBackgroundColor(color: number): void;
    getForegroundColor(): number;
    setForegroundColor(color: number): void;
  }

  export interface TopBarIosFFI {
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
      postMessage(noValue: null): Promise<Data.DataString<TopBarItem[]>>;
    };
    customNaviActions: {
      postMessage(actionList: Data.DataString<TopBarItem[]>): void;
    };
    getNaviBackgroundColor: {
      postMessage(noValue: null): Promise<Color.RGBAHex>;
    };
    updateNaviBarBackgroundColor: {
      postMessage(colorHex: Color.RGBAHex): void;
    };
    getNaviForegroundColor: {
      postMessage(noValue: null): Promise<Color.RGBAHex>;
    };
    updateNaviBarTintColor: {
      postMessage(colorHex: Color.RGBAHex): void;
    };
  }

  export interface TopBarDesktopFFI {
    setTopbarHidden(
      scopedValue: boolean | undefined,
      globalValue?: boolean
    ): Promise<void>;
    getTopbarHidden(): Promise<boolean>;
    setTopbarOverlay(
      scopedValue: boolean | undefined,
      globalValue?: boolean
    ): Promise<void>;
    getTopbarOverlay(): Promise<boolean>;
    setTopbarTitle(
      scopedValue: string | undefined,
      globalValue?: string
    ): Promise<void>;
    getTopbarTitle(): Promise<string>;
    setTopbarHeight(
      scopedValue: string | undefined,
      globalValue?: string
    ): Promise<string>;
    getTopbarHeight(): Promise<string>;
    setTopbarBackgroundColor(
      scopedValue: string | undefined,
      globalValue?: string
    ): Promise<void>;
    getTopbarBackgroundColor(): Promise<Color.ColorFormatType>;
    setTopbarForegroundColor(
      scopedValue: string | undefined,
      globalValue?: string
    ): Promise<void>;
    getTopbarForegroundColor(): Promise<Color.ColorFormatType>;
  }

  export interface ITopBarFFI {
    back(): Promise<void>;
    getEnabled(): Promise<boolean>;
    toggleEnabled(): Promise<void>;
    setHidden(): Promise<void>;
    getOverlay(): Promise<boolean>;
    toggleOverlay(): Promise<void>;
    setOverlay(alpha:string): Promise<void>;
    getTitle(): Promise<string>;
    setTitle(title: string): Promise<void>;
    hasTitle(): Promise<boolean>;
    getHeight(): Promise<number>;
    getActions(): Promise<TopBarItem[]>;
    setActions(actionList: TopBarItem[]): Promise<void>;
    getBackgroundColor(): Promise<Color.RGBAHex>;
    setBackgroundColor(color: Color.RGBAHex): Promise<void>;
    getForegroundColor(): Promise<Color.RGBAHex>;
    setForegroundColor(color: Color.RGBAHex): Promise<void>;
  }
}
