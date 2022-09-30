import { convertToRGBAHex } from "../util/index.ts";
import { DwebPlugin } from "../native/dweb-plugin.ts";
import { StatusBarFFI } from "!statusbar/net";
import { StatusBar } from "./bfcsStatusBarType.ts";
import { Color } from "../types/colorType.ts";

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
  /**
   * 设置状态栏颜色
   * @param color string
   * @param barStyle enum(light-content,dark-content)
   * @returns
   */
  async setStatusBarColor(
    color?: string,
    barStyle?: StatusBar.StatusBarStyle,
  ): Promise<void> {
    const colorHex = convertToRGBAHex(color ?? "");
    await this._ffi.setStatusBarColor(colorHex, barStyle);
    return;
  }
  /**获取状态栏颜色 */
  async getStatusBarColor(): Promise<Color.RGBAHex> {
    const colorHex = await this._ffi.getStatusBarColor();
    return colorHex;
  }
  /**查看状态栏是否可见 */
  async getStatusBarVisible(): Promise<boolean> {
    const isVisible = await this._ffi.getStatusBarVisible();
    return isVisible;
  }
  /**设置状态栏是否可见 */
  async setStatusBarVisible(isVer = true): Promise<boolean> {
    return await this._ffi.setStatusBarVisible(isVer);
  }
  /**查看状态栏是否覆盖内容 */
  async getStatusBarOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getStatusBarOverlay();
    return overlay;
  }
  /**设置状态栏是否覆盖内容 */
  async setStatusBarOverlay(isOver = false): Promise<boolean> {
    const overlay = await this._ffi.setStatusBarOverlay(isOver);
    return overlay;
  }
  /**获取状态栏是否更偏向于使用黑色效果 */
  async getStatusBarIsDark(): Promise<StatusBar.StatusBarStyle> {
    const barStyle = await this._ffi.getStatusBarIsDark();
    return barStyle;
  }

  static get observedAttributes() {
    return ["overlay", "hidden", "bar-style", "background-color"];
  }

  /**当自定义元素增加、删除、自身属性修改时，被调用。 */
  attributeChangedCallback(attrName: string, _oldVal: unknown, newVal: unknown) {
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
          newVal ? (newVal as StatusBar.StatusBarStyle) : undefined,
        );
      }
    } else if (attrName === "background-color") {
      if (this.hasAttribute(attrName)) {
        const barStyle = this.getAttribute("bar-style")
          ? (this.getAttribute("bar-style") as StatusBar.StatusBarStyle)
          : undefined;
        this.setStatusBarColor(newVal as string, barStyle);
      }
    }
  }
}