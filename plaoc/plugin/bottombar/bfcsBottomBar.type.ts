declare namespace Plaoc {
  interface BottomBarAndroidFFI {
    getEnabled(): boolean;
    toggleEnabled(isEnabled: number): void;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    getHeight(): number;
    setHeight(heightDp: number): void;
    getActions(): Plaoc.DataString<BottomBarItem[]>;
    setActions(actionList: Plaoc.DataString<BottomBarItem[]>): void;
    getBackgroundColor(): number;
    setBackgroundColor(color: number): void;
    getForegroundColor(): number;
    setForegroundColor(color: number): void;
  }

  interface BottomBarIosFFI {
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
      postMessage(noValue: null): Promise<Plaoc.DataString<BottomBarItem[]>>;
    };
    customBottomActions: {
      postMessage(actionList: Plaoc.DataString<BottomBarItem[]>): void;
    };
    getBottomBarBackgroundColor: {
      postMessage(noValue: null): Promise<Plaoc.RGBAHex>;
    };
    updateBottomViewBackgroundColor: {
      postMessage(colorHex: Plaoc.RGBAHex): void;
    };
    getBottomViewForegroundColor: {
      postMessage(noValue: null): Promise<Plaoc.RGBAHex>;
    };
    updateBottomViewForegroundColor: {
      postMessage(colorHex: Plaoc.RGBAHex): void;
    };
  }

  interface IBottomBarFFI {
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
    getBackgroundColor(): Promise<Plaoc.RGBAHex>;
    setBackgroundColor(color: Plaoc.RGBAHex): Promise<void>;
    getForegroundColor(): Promise<Plaoc.RGBAHex>;
    setForegroundColor(color: Plaoc.RGBAHex): Promise<void>;
  }

  type BottomBarColorType = number | Plaoc.RGBAHex;

  interface IBottomBarColors {
    indicatorColor?: BottomBarColorType;
    iconColor?: BottomBarColorType;
    iconColorSelected?: BottomBarColorType;
    textColor?: BottomBarColorType;
    textColorSelected?: BottomBarColorType;
  }

  interface BottomBarItem {
    icon: Plaoc.IPlaocIcon;
    onClickCode: string;
    label?: string;
    selected?: boolean;
    selectable?: boolean;
    disabled?: boolean;
    colors?: IBottomBarColors;
  }
}

declare const bottom_bar: Plaoc.BottomBarAndroidFFI;
