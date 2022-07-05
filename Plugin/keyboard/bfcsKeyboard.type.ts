declare namespace Plaoc {
  interface IKeyboardSafeArea {
    top: number;
    left: number;
    right: number;
    bottom: number;
  }

  interface VirtualKeyboardAndroidFFI {
    getSafeArea(): Plaoc.DataString<IKeyboardSafeArea>;
    getHeight(): number;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): void;
    show(): void;
    hide(): void;
  }

  interface VirtualKeyboardIosFFI {
    keyboardSafeArea: {
      postMessage(noValue: null): Promise<IKeyboardSafeArea>;
    };
    keyHeight: {
      postMessage(noValue: null): Promise<number>;
    };
    getKeyboardOverlay: {
      postMessage(noValue: null): Promise<boolean>;
    };
    setKeyboardOverlay: {
      postMessage(overlay: string): void;
    };
    // show
    hideKeyboard: {
      postMessage(noValue: null): void;
    };
  }

  interface IVirtualKeyboardFFI {
    getKeyboardSafeArea(): Promise<IKeyboardSafeArea>;
    getKeyboardHeight(): Promise<number>;
    getKeyboardOverlay(): Promise<boolean>;
    toggleKeyboardOverlay(): Promise<void>;
    setKeyboardOverlay(): Promise<void>;
    showKeyboard(): Promise<void>;
    hideKeyboard(): Promise<void>;
  }
}

declare const virtual_keyboard: Plaoc.VirtualKeyboardAndroidFFI;
