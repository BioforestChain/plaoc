declare namespace Bfcs {
  type BottomBarFFI = {
    getEnabled(): boolean;
    toggleEnabled(isEnabled: number): void;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    getHeight(): number;
    setHeight(heightDp: number): void;
    getActions(): DataString<BottomBarAction[]>;
    setActions(actionList: DataString<BottomBarAction[]>): void;
    getBackgroundColor(): number;
    setBackgroundColor(color: number): void;
    getForegroundColor(): number;
    setForegroundColor(color: number): void;
  };

  type DataString<T> = string;

  interface DwebIcon {
    source: string;
    type: IconType;
    description?: string;
    size?: number;
  }

  enum IconType {
    NamedIcon = "NamedIcon",
    AssetIcon = "AssetIcon",
  }

  interface Colors {
    indicatorColor?: number;
    iconColor?: number;
    iconColorSelected?: number;
    textColor?: number;
    textColorSelected?: number;
  }

  interface BottomBarAction {
    icon: DwebIcon;
    onClickCode: string;
    label: string;
    selected: boolean;
    selectable: boolean;
    disabled: boolean;
    color?: Colors;
  }
}

declare const bottom_bar: Bfcs.BottomBarFFI;
