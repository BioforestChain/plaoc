declare namespace Bfcs {
  type TopBarFFI = {
    back(): void;
    getEnabled(): boolean;
    toggleEnabled(isEnabled: number): boolean;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): boolean;
    getTitle(): string;
    setTitle(title: string): void;
    hasTitle(): boolean;
    getHeight(): number;
    getActions(): string;
    setActions(action: string): void;
    getBackgroundColor(): number;
    setBackgroundColor(color: number): void;
    getForegroundColor(): number;
    setForegroundColor(color: number): void;
  };

  type ValueType = boolean | string | number;
}

declare const top_bar: Bfcs.TopBarFFI;
