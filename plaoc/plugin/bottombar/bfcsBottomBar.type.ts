import { Icon } from "../icon/bfspIcon.type";
export namespace BottomBar {
  export interface BottomBarAndroidFFI {
    getEnabled(): boolean;
    toggleEnabled(isEnabled: number): void;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    getHeight(): number;
    setHeight(heightDp: number): void;
    getActions(): Data.DataString<BottomBarItem[]>;
    setActions(actionList: Data.DataString<BottomBarItem[]>): void;
    getBackgroundColor(): number;
    setBackgroundColor(color: number): void;
    getForegroundColor(): number;
    setForegroundColor(color: number): void;
  }

  export interface BottomBarIosFFI {
    getBottomBarEnabled: {
      postMessage(noValue: null): Promise<boolean>;
    };
    hiddenBottomView: {
      postMessage(hidden: string): void;
    };
    getBottomBarOverlay: {
      postMessage(noValue: null): Promise<boolean>;
    };
    updateBottomViewOverlay: {
      postMessage(overlay: number): void;
    };
    bottomHeight: {
      postMessage(noValue: null): Promise<number>;
    };
    updateBottomViewHeight: {
      postMessage(height: number): void;
    };
    getBottomActions: {
      postMessage(noValue: null): Promise<Data.DataString<BottomBarItem[]>>;
    };
    customBottomActions: {
      postMessage(actionList: Data.DataString<BottomBarItem[]>): void;
    };
    getBottomBarBackgroundColor: {
      postMessage(noValue: null): Promise<Color.RGBAHex>;
    };
    updateBottomViewBackgroundColor: {
      postMessage(colorHex: Color.RGBAHex): void;
    };
    getBottomViewForegroundColor: {
      postMessage(noValue: null): Promise<Color.RGBAHex>;
    };
    updateBottomViewForegroundColor: {
      postMessage(colorHex: Color.RGBAHex): void;
    };
  }

  export interface IBottomBarFFI {
    getEnabled(): Promise<boolean>;
    toggleEnabled(isEnabled: number): Promise<void>;
    setHidden(): Promise<void>;
    getOverlay(): Promise<boolean>;
    toggleOverlay(isOverlay: number): Promise<void>;
    setOverlay(): Promise<void>;
    getHeight(): Promise<number>;
    setHeight(heightDp: number): Promise<void>;
    getActions(): Promise<BottomBarItem[]>;
    setActions(actionList: BottomBarItem[]): Promise<void>;
    getBackgroundColor(): Promise<Color.RGBAHex>;
    setBackgroundColor(color: Color.RGBAHex): Promise<void>;
    getForegroundColor(): Promise<Color.RGBAHex>;
    setForegroundColor(color: Color.RGBAHex): Promise<void>;
  }

  export type BottomBarColorType = number | Color.RGBAHex;

  export interface IBottomBarColors {
    indicatorColor?: BottomBarColorType;
    iconColor?: BottomBarColorType;
    iconColorSelected?: BottomBarColorType;
    textColor?: BottomBarColorType;
    textColorSelected?: BottomBarColorType;
  }

  export interface BottomBarItem {
    icon: Icon.IPlaocIcon;
    onClickCode: string;
    label?: string;
    selected?: boolean;
    selectable?: boolean;
    disabled?: boolean;
    colors?: IBottomBarColors;
  }
}

declare const bottom_bar: BottomBar.BottomBarAndroidFFI;
