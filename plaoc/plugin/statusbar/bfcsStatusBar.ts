import { convertToRGBAHex } from "./../util";
import { DwebPlugin } from "../native/dweb-plugin";
import { StatusBarFFI } from "./ffi#android";

export class BfcsStatusBar extends DwebPlugin {
  private _ffi: Plaoc.IStatusBarFFI;

  constructor() {
    super();

    this._ffi = new StatusBarFFI();
  }
  /**每次将 Web 组件附加到 DOM 时，都会调用一次该 方法 */
  connectedCallback() {
    this._init();
  }
  /**移除DOM添加的方法 */
  disconnectedCallback() {}

  private async _init() {
    // const colorHex = await this.getStatusBarColor();
    // this.setAttribute("background-color", colorHex);
    const barStyle = await this.getStatusBarStyle();
    this.setAttribute("bar-style", barStyle);
  }

  async setStatusBarColor(
    color?: string,
    barStyle?: Plaoc.StatusBarStyle
  ): Promise<void> {
    const colorHex = convertToRGBAHex(color ?? "");
    await this._ffi.setStatusBarColor(colorHex, barStyle);

    return;
  }

  async getStatusBarColor(): Promise<Plaoc.RGBAHex> {
    const colorHex = await this._ffi.getStatusBarColor();

    return colorHex;
  }

  async getStatusBarVisible(): Promise<boolean> {
    const isVisible = await this._ffi.getStatusBarVisible();

    return isVisible;
  }

  async toggleStatusBarVisible(): Promise<void> {
    await this._ffi.toggleStatusBarVisible();

    return;
  }

  async getStatusBarOverlay(): Promise<boolean> {
    let overlay = await this._ffi.getStatusBarOverlay();

    return overlay;
  }

  async toggleStatusBarOverlay(): Promise<void> {
    await this._ffi.toggleStatusBarOverlay();

    return;
  }

  async getStatusBarStyle(): Promise<Plaoc.StatusBarStyle> {
    const barStyle = await this._ffi.getStatusBarStyle();

    return barStyle;
  }

  static get observedAttributes() {
    return ["overlay", "hidden", "bar-style", "background-color"];
  }
  /**当自定义元素增加、删除、自身属性修改时，被调用。 */
  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        this._ffi.setStatusBarOverlay();
      }
    } else if (attrName === "hidden") {
      if (this.hasAttribute(attrName)) {
        this._ffi.setStatusBarHidden();
      }
    } else if (attrName === "bar-style") {
      if (this.hasAttribute(attrName)) {
        this.setStatusBarColor(
          this.getAttribute("background-color") ?? "",
          newVal ? (newVal as Plaoc.StatusBarStyle) : undefined
        );
      }
    } else if (attrName === "background-color") {
      if (this.hasAttribute(attrName)) {
        let barStyle = this.getAttribute("bar-style")
          ? (this.getAttribute("bar-style") as Plaoc.StatusBarStyle)
          : undefined;
        this.setStatusBarColor(newVal as string, barStyle);
      }
    }
  }
}
