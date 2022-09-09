import { convertToRGBAHex } from "./../util";
import { DwebPlugin } from "../native/dweb-plugin";
import { StatusBarFFI } from "./net";
import { StatusBar } from "./bfcsStatusBar.type";
import "../typings";
import { Color } from "../typings/types/color.type";

export class BfcsStatusBar extends DwebPlugin {
  private _ffi: StatusBar.IStatusBarFFI;

  constructor() {
    super();
    this._ffi = new StatusBarFFI();
  }
  /**每次将 Web 组件附加到 DOM 时，都会调用一次该 方法 */
  connectedCallback() {
    this._init();
  }
  /**移除DOM添加的方法 */
  disconnectedCallback() { }

  private async _init() {
    // this.setAttribute("background-color", colorHex);
    const barStyle = await this.getStatusBarIsDark();
    this.setAttribute("bar-style", barStyle);
  }

  async setStatusBarColor(
    color?: string,
    barStyle?: StatusBar.StatusBarStyle
  ): Promise<void> {
    const colorHex = convertToRGBAHex(color ?? "");
    await this._ffi.setStatusBarColor(colorHex, barStyle);
    return;
  }

  async getStatusBarColor(): Promise<Color.RGBAHex> {
    const colorHex = await this._ffi.getStatusBarColor();
    return colorHex;
  }

  async getStatusBarVisible(): Promise<boolean> {
    const isVisible = await this._ffi.getStatusBarVisible();
    return isVisible;
  }

  async setStatusBarVisible(isVer: boolean = true): Promise<boolean> {
    return await this._ffi.setStatusBarVisible(isVer);
  }

  async getStatusBarOverlay(): Promise<boolean> {
    let overlay = await this._ffi.getStatusBarOverlay();

    return overlay;
  }

  async setStatusBarOverlay(isOver: boolean = false): Promise<boolean> {
    const overlay = await this._ffi.setStatusBarOverlay(isOver);
    return overlay
  }

  async getStatusBarIsDark(): Promise<StatusBar.StatusBarStyle> {
    const barStyle = await this._ffi.getStatusBarIsDark();

    return barStyle;
  }

  static get observedAttributes() {
    return ["overlay", "hidden", "bar-style", "background-color"];
  }

  /**当自定义元素增加、删除、自身属性修改时，被调用。 */
  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        this._ffi.setStatusBarOverlay(true);
      }
    } else if (attrName === "hidden") {
      if (this.hasAttribute(attrName)) {
        this._ffi.setStatusBarHidden();
      }
    } else if (attrName === "bar-style") {
      if (this.hasAttribute(attrName)) {
        this.setStatusBarColor(
          this.getAttribute("background-color") ?? "",
          newVal ? (newVal as StatusBar.StatusBarStyle) : undefined
        );
      }
    } else if (attrName === "background-color") {
      if (this.hasAttribute(attrName)) {
        let barStyle = this.getAttribute("bar-style")
          ? (this.getAttribute("bar-style") as StatusBar.StatusBarStyle)
          : undefined;
        this.setStatusBarColor(newVal as string, barStyle);
      }
    }
  }
}
