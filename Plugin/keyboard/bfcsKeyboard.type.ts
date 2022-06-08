declare namespace Bfcs {
  type VirtualKeyboardFFI = {
    getSafeArea(): DataString<KeyboardSafeArea>;
    getHeight(): number;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    show(): void;
    hide(): void;
  };

  type DataString<T> = string;

  interface KeyboardSafeArea {
    top: number;
    left: number;
    right: number;
    bottom: number;
  }
}

declare const virtual_keyboard: Bfcs.VirtualKeyboardFFI;
