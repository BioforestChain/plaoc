import { Icon } from "../icon/bfspIconType";
import { Color } from "../types/colorType";
import { Data } from "../types/dataType";

export namespace TopBar {
  export interface TopBarItem {
    icon: Icon.IPlaocIcon;
    onClickCode: string;
    disabled?: boolean;
  }

  export interface TopBarAndroidFFI {
    topBarNavigationBack(): void;
    getTopBarEnabled(): boolean;
    setTopBarEnabled(isEnabled: number): void;
    getTopBarOverlay(): boolean;
    setTopBarOverlay(alpha: string): void;
    getTopBarTitle(): string;
    setTopBarTitle(title: string): void;
    hasTopBarTitle(): boolean;
    getTopBarHeight(): number;
    getTopBarActions(): Data.DataString<TopBarItem[]>;
    setTopBarActions(actionList: Data.DataString<TopBarItem[]>): void;
    getTopBarBackgroundColor(): number;
    setTopBarBackgroundColor(color: number): void;
    getTopBarForegroundColor(): number;
    setTopBarForegroundColor(color: number): void;
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
    topBarNavigationBack(): Promise<boolean>;
    getTopBarEnabled(): Promise<boolean>;
    setTopBarEnabled(isEnabled: boolean): Promise<void>;
    setTopBarHidden(): Promise<void>;
    getTopBarOverlay(): Promise<boolean>;
    setTopBarOverlay(alpha: string): Promise<void>;
    getTopBarTitle(): Promise<string>;
    setTopBarTitle(title: string): Promise<void>;
    hasTopBarTitle(): Promise<boolean>;
    getTopBarHeight(): Promise<number>;
    getTopBarActions(): Promise<TopBarItem[]>;
    setTopBarActions(actionList: TopBarItem[]): Promise<void>;
    getTopBarBackgroundColor(): Promise<Color.RGBAHex>;
    setTopBarBackgroundColor(color: Color.RGBAHex): Promise<void>;
    getTopBarForegroundColor(): Promise<Color.RGBAHex>;
    setTopBarForegroundColor(color: Color.RGBAHex): Promise<void>;
  }
}
