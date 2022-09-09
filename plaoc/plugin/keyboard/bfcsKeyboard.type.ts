import { Data } from "../typings/types/data.type";

export namespace Keyboard {
  export interface IKeyboardSafeArea {
    top: number;
    left: number;
    right: number;
    bottom: number;
  }

  export interface VirtualKeyboardAndroidFFI {
    getSafeArea(): Data.DataString<IKeyboardSafeArea>;
    getHeight(): number;
    getOverlay(): boolean;
    toggleOverlay(isOverlay: number): boolean;
    show(): void;
    hide(): void;
  }

  export interface VirtualKeyboardIosFFI {
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

  export interface IVirtualKeyboardFFI {
    getKeyboardSafeArea(): Promise<IKeyboardSafeArea>;
    getKeyboardHeight(): Promise<number>;
    getKeyboardOverlay(): Promise<boolean>;
    toggleKeyboardOverlay(): Promise<void>;
    setKeyboardOverlay(): Promise<boolean>;
    showKeyboard(): Promise<boolean>;
    hideKeyboard(): Promise<boolean>;
  }
}
