import { VirtualKeyboardFFI } from "./ffi";
import { Keyboard } from "./bfcsKeyboard.type";
import { DwebPlugin } from '../native/dweb-plugin';

export class BfcsKeyboard extends DwebPlugin {
  private _ffi: Keyboard.IVirtualKeyboardFFI;

  constructor() {
    super();

    this._ffi = new VirtualKeyboardFFI();
    this._init();
  }

  connectedCallback() {}

  disconnectedCallback() {}

  private async _init() {
    this.setAttribute("hidden", "");
  }

  async getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    const safeArea = await this._ffi.getKeyboardSafeArea();

    return safeArea;
  }

  async getKeyboardHeight(): Promise<number> {
    const height = await this._ffi.getKeyboardHeight();

    return height;
  }

  async getKeyboardOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getKeyboardOverlay();

    return overlay;
  }

  async toggleKeyboardOverlay(): Promise<void> {
    await this._ffi.toggleKeyboardOverlay();

    return;
  }

  async showKeyboard(): Promise<void> {
    this.removeAttribute("hidden");
    await this._ffi.showKeyboard();

    return;
  }

  async hideKeyboard(): Promise<void> {
    this.setAttribute("hidden", "");
    await this._ffi.hideKeyboard();

    return;
  }

  /**
   * @Todo synchronized with the keyboard animation
   * @link https://developer.android.com/training/system-ui/sw-keyboard
   */

  static get observedAttributes() {
    return ["overlay", "hidden"];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "overlay" && oldVal !== newVal) {
      if (this.hasAttribute(attrName)) {
        this._ffi.setKeyboardOverlay();
      }
    } else if (attrName === "hidden" && oldVal !== newVal) {
      if (this.hasAttribute(attrName)) {
        this.hideKeyboard();
      }
    }
  }
}
