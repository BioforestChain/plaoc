import { convertToRGBAHex } from "./../util";

import { TopBarFFI } from "./ffi";
import { TopBar } from "./bfcsTopBar.type";
import "../typings";
import { Icon } from "../icon/bfspIcon.type";
import { Color } from "../typings/types/color.type";
import { DwebPlugin } from "../native/dweb-plugin";

export class BfcsTopBar extends DwebPlugin {
  private _ffi: TopBar.ITopBarFFI;
  private _observer: MutationObserver;
  private _actionList: TopBar.TopBarItem[] = [];

  constructor() {
    super();

    this._ffi = new TopBarFFI();
    this._observer = new MutationObserver(async (mutations) => {
      console.log("BfcsTopBar MutationObserver: =>");
      await this.collectActions();
    });
  }

  connectedCallback() {
    console.log("BfcsTopBar connectedCallback:", this._observer);
    this._observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
      attributeFilter: [
        "disabled",
        "type",
        "description",
        "size",
        "source",
        "height",
      ],
    });

    this._init();
  }

  disconnectedCallback() {
    this._observer.disconnect();
  }

  private async _init() {
    const height = await this.getHeight();
    this.setAttribute("height", `${height}`);

    // const backgroundColor = await this.getBackgroundColor();
    // this.setAttribute("background-color", backgroundColor);

    // const foregroundColor = await this.getForegroundColor();
    // this.setAttribute("foreground-color", foregroundColor);
  }

  async back(): Promise<void> {
    await this._ffi.back();

    return;
  }

  async toggleEnabled(): Promise<void> {
    await this._ffi.toggleEnabled();

    return;
  }

  async getEnabled(): Promise<boolean> {
    const isEnabled = await this._ffi.getEnabled();

    return isEnabled;
  }

  async getTitle(): Promise<string> {
    const title = await this._ffi.getTitle();

    return title;
  }

  async setTitle(title: string): Promise<void> {
    await this._ffi.setTitle(title);

    return;
  }

  async hasTitle(): Promise<boolean> {
    const has = await this._ffi.hasTitle();

    return has;
  }

  async getOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getOverlay();

    return overlay;
  }

  async toggleOverlay(): Promise<void> {
    await this._ffi.toggleOverlay();

    return;
  }

  async getHeight(): Promise<number> {
    const height = await this._ffi.getHeight();

    return height;
  }

  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const color = await this._ffi.getBackgroundColor();

    return color;
  }

  async setBackgroundColor(color: string): Promise<void> {
    const colorHex = convertToRGBAHex(color);
    await this._ffi.setBackgroundColor(colorHex);

    return;
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    const color = await this._ffi.getForegroundColor();

    return color;
  }

  async setForegroundColor(color: string): Promise<void> {
    const colorHex = convertToRGBAHex(color);
    await this._ffi.setForegroundColor(colorHex);

    return;
  }

  async getActions(): Promise<TopBar.TopBarItem[]> {
    this._actionList = await this._ffi.getActions();

    return this._actionList;
  }

  async setActions(): Promise<void> {
    await this._ffi.setActions(this._actionList);

    return;
  }

  async collectActions() {
    this._actionList = [];
    console.log(
      "this.querySelectorAll:",
      JSON.stringify(this.querySelectorAll("dweb-top-bar-button"))
    );
    this.querySelectorAll("dweb-top-bar-button").forEach((childNode) => {
      let icon: Icon.IPlaocIcon = {
        source: "",
        // type: TopBar.IconType.NamedIcon,
        type: "NamedIcon" as Icon.IconType.NamedIcon,
      };

      if (childNode.querySelector("dweb-icon")) {
        let $ = childNode.querySelector("dweb-icon");

        icon.source = $?.getAttribute("source") ?? "";
        icon.type = $?.hasAttribute("type")
          ? ($.getAttribute("type") as Icon.IconType)
          : ("NamedIcon" as Icon.IconType.NamedIcon);
        icon.description = $?.getAttribute("description") ?? "";
        icon.size = $?.hasAttribute("size")
          ? ($.getAttribute("size") as unknown as number)
          : undefined;
      }

      const bid = childNode.getAttribute("bid");
      const onClickCode = `document.querySelector('dweb-top-bar-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
      const disabled = childNode.hasAttribute("disabled") ? true : false;
      this._actionList.push({ icon, onClickCode, disabled });
      console.log("icon, disabled:", icon, disabled);
    });

    await this.setActions();
  }

  static get observedAttributes() {
    return [
      "title",
      "hidden",
      "background-color",
      "foreground-color",
      "overlay",
    ];
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {
    if (oldVal === newVal) {
      return;
    }

    if (attrName === "title") {
      await this.setTitle(newVal as string);
    } else if (attrName === "background-color") {
      await this.setBackgroundColor(newVal as string);
    } else if (attrName === "foreground-color") {
      await this.setForegroundColor(newVal as string);
    } else if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        await this._ffi.setOverlay();
      }
    } else if (attrName === "hidden") {
      if (this.hasAttribute(attrName)) {
        await this._ffi.setHidden();
      }
    }
  }
}
