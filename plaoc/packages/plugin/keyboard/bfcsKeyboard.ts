import { VirtualKeyboardFFI } from "!keyboard/net";
import { Keyboard } from "./bfcsKeyboardType.ts";
import { DwebPlugin } from "../native/dweb-plugin.ts";

export class BfcsKeyboard extends DwebPlugin {
  private _ffi: Keyboard.IVirtualKeyboardFFI;

  constructor() {
    super();

    this._ffi = new VirtualKeyboardFFI();
  }

  connectedCallback() { }

  disconnectedCallback() { }

  /**获取键盘安全区域 */
  async getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    const safeArea = await this._ffi.getKeyboardSafeArea();
    return safeArea;
  }
  /**获取键盘高度 */
  async getKeyboardHeight(): Promise<number> {
    const height = await this._ffi.getKeyboardHeight();
    return height;
  }
  /**获取键盘是否覆盖了内容 */
  async getKeyboardOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getKeyboardOverlay();
    return overlay;
  }
  /**获取键盘高度 */
  async setKeyboardOverlay(): Promise<void> {
    await this._ffi.toggleKeyboardOverlay();
    return;
  }
  /**显示键盘 */
  async showKeyboard(): Promise<boolean> {
    this.removeAttribute("hidden");
    return await this._ffi.showKeyboard();
  }
  /**隐藏键盘 */
  async hideKeyboard(): Promise<boolean> {
    this.setAttribute("hidden", "");
    return await this._ffi.hideKeyboard();
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
      } else {
        this.showKeyboard();
      }
    }
  }
}