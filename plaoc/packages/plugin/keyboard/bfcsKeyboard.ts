import { VirtualKeyboardNet } from "./net.ts";
import { Keyboard } from "./bfcsKeyboardType.ts";
import { DwebPlugin } from "../native/dweb-plugin.ts";

export class BfcsKeyboard extends DwebPlugin {
  private net: Keyboard.IVirtualKeyboardNet;

  constructor() {
    super();

    this.net = new VirtualKeyboardNet();
  }

  connectedCallback() {}

  disconnectedCallback() {}

  /**获取键盘安全区域 */
  async getKeyboardSafeArea(): Promise<Keyboard.IKeyboardSafeArea> {
    const safeArea = await this.net.getKeyboardSafeArea();
    return safeArea;
  }
  /**获取键盘高度 */
  async getKeyboardHeight(): Promise<number> {
    const height = await this.net.getKeyboardHeight();
    return height;
  }
  /**获取键盘是否覆盖了内容 */
  async getKeyboardOverlay(): Promise<boolean> {
    const overlay = await this.net.getKeyboardOverlay();
    return overlay;
  }
  /**设置键盘是否覆盖了内容 */
  async setKeyboardOverlay(isOverlay: boolean): Promise<boolean> {
    return await this.net.setKeyboardOverlay(isOverlay);
  }
  /**显示键盘 */
  async showKeyboard(): Promise<boolean> {
    this.removeAttribute("hidden");
    return await this.net.showKeyboard();
  }
  /**隐藏键盘 */
  async hideKeyboard(): Promise<boolean> {
    this.setAttribute("hidden", "");
    return await this.net.hideKeyboard();
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
        this.net.toggleKeyboardOverlay();
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
