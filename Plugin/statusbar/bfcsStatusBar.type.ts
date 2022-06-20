declare namespace Plaoc {
  type StatusBarFFI = {
    setStatusBarColor(colorHex: number, darkIcons: number): void;
    getStatusBarVisible(): boolean;
    toggleStatusBarVisible(visible: number): void;
    getStatusBarOverlay(): boolean;
    toggleStatusBarOverlay(isOverlay: number): void;
  };
}

declare const system_ui: Plaoc.StatusBarFFI;
