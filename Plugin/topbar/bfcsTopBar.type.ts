declare namespace Bfcs {
  type TopBarFFI = {
    back(): void;
    getEnabled(): boolean;
    toggleEnabled(isEnabled: number): void;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    getTitle(): string;
    setTitle(title: string): void;
    hasTitle(): boolean;
    getHeight(): number;
    getActions(): DataString<TopBarAction[]>;
    setActions(actionList: DataString<TopBarAction[]>): void;
    getBackgroundColor(): number;
    setBackgroundColor(color: number): void;
    getForegroundColor(): number;
    setForegroundColor(color: number): void;
  };

  type DataString<T> = string;

  enum IconType {
    NamedIcon = "NamedIcon",
    AssetIcon = "AssetIcon",
  }

  interface DwebIcon {
    source: string;
    type: IconType;
    description?: string;
    size?: number;
  }

  interface TopBarAction {
    icon: DwebIcon;
    onClickCode: string;
    disabled: boolean;
  }
}

declare const top_bar: Bfcs.TopBarFFI;
