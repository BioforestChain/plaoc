import { convertToRGBAHex } from "@plaoc/plugin-util";

import { StatusBarFFI } from "./ffi";

export class BfcsStatusBar extends HTMLElement {
  private _ffi: Plaoc.IStatusBarFFI;

  constructor() {
    super();

    this._ffi = new StatusBarFFI();
  }

  connectedCallback() {}

  disconnectedCallback() {}

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
