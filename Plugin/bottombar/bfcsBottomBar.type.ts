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
    updateBottomViewAlpha: {
      postMessage(alpha: string): void;
    };
    bottomHeight: {
      postMessage(noValue: null): Promise<number>;
    };
    updateBottomViewHeight: {
      postMessage(height: number): void;
    };
    getBottomActions: {
      postMessage(noValue: null): Promise<BottomBarItem[]>;
    };
    customBottomActions: {
      postMessage(actionList: BottomBarItem[]): void;
    };
    getBottomBarBackgroundColor: {
      postMessage(noValue: null): Promise<Plaoc.IColor>;
    };
    updateBottomViewBackgroundColor: {
      postMessage(colorObject: Plaoc.IColor): void;
    };
    getBottomViewForegroundColor: {
      postMessage(noValue: null): Promise<Plaoc.IColor>;
    };
    updateBottomViewForegroundColor: {
      postMessage(colorObject: Plaoc.IColor): void;
    };
  }

  interface Colors {
    indicatorColor?: number;
    iconColor?: number;
    iconColorSelected?: number;
    textColor?: number;
    textColorSelected?: number;
  }

  interface BottomBarItem {
    icon: Plaoc.IPlaocIcon;
    onClickCode: string;
    label?: string;
    selected?: boolean;
    selectable?: boolean;
    disabled?: boolean;
    colors?: Colors;
  }
}

declare const bottom_bar: Plaoc.BottomBarAndroidFFI;
