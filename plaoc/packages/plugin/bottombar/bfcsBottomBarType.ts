import { Icon } from "../icon/bfspIconType.ts";
import { Color } from "../types/colorType.ts";
import { Data } from "../types/dataType.ts";
export namespace BottomBar {
  export interface BottomBarAndroidFFI {
    getEnabled(): boolean;
    toggleEnabled(isEnabled: boolean): void;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: string): void;
    getHeight(): number;
    setHeight(heightDp: string): void;
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
      postMessage(noValue: null): Promise<number>;
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


  export interface IBottomBarNet {
    getHidden(): Promise<boolean>;
    setHidden(isEnabled: boolean): Promise<boolean>;
    getBottomBarAlpha(): Promise<number>;
    setBottomBarAlpha(alpha: string): Promise<number>;
    getHeight(): Promise<number>;
    setHeight(heightDp: number): Promise<boolean>;
    getActions(): Promise<BottomBarItem[]>;
    setActions(actionList: BottomBarItem[]): Promise<void>;
    getBackgroundColor(): Promise<Color.RGBAHex>;
    setBackgroundColor(color: Color.RGBAHex): Promise<boolean>;
    getForegroundColor(): Promise<Color.RGBAHex>;
    setForegroundColor(color: Color.RGBAHex): Promise<boolean>;
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
    alwaysShowLabel: boolean;
    label?: string;
    selected?: boolean;
    selectable?: boolean;
    disabled?: boolean;
    colors?: IBottomBarColors;
  }
}

declare const bottom_bar: BottomBar.BottomBarAndroidFFI;
