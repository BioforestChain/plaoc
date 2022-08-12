export namespace Plaoc {
  export type VirtualKeyboardFFI = {
    getSafeArea(): DataString<KeyboardSafeArea>;
    getHeight(): number;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    show(): void;
    hide(): void;
  };

  export type DataString<T> = string;

  export interface KeyboardSafeArea {
    top: number;
    left: number;
    right: number;
    bottom: number;
  }
}

export const virtual_keyboard: Plaoc.VirtualKeyboardFFI = "";
