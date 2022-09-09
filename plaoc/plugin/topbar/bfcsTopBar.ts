import { TopBarFFI } from "./net";
import { TopBar } from "./bfcsTopBar.type";
import "../typings";
import { Icon } from "../icon/bfspIcon.type";
import { Color } from "../typings/types/color.type";
import { DwebPlugin } from "../native/dweb-plugin";
import { convertToRGBAHex } from "./../util";

export class BfcsTopBar extends DwebPlugin {
  private _ffi: TopBar.ITopBarFFI;
  private _observer: MutationObserver;
  private _actionList: TopBar.TopBarItem[] = [];

  constructor() {
    super();

    this._ffi = new TopBarFFI();
    this._observer = new MutationObserver(async (mutations) => {
      await this.collectActions();
    });
  }

  connectedCallback() {
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
    const height = await this.getTopBarHeight();
    this.setAttribute("height", `${height}`);
  }

  async topBarNavigationBack(): Promise<void> {
    await this._ffi.topBarNavigationBack();
    return;
  }

  async setTopBarEnabled(isEnabled: boolean): Promise<void> {
    await this._ffi.setTopBarEnabled(isEnabled);
    return;
  }

  async getTopBarEnabled(): Promise<boolean> {
    const isEnabled = await this._ffi.getTopBarEnabled();
    return isEnabled;
  }

  async getTopBarTitle(): Promise<string> {
    const title = await this._ffi.getTopBarTitle();
    return title;
  }

  async setTopBarTitle(title: string): Promise<void> {
    await this._ffi.setTopBarTitle(title);
    return;
  }

  async hasTopBarTitle(): Promise<boolean> {
    const has = await this._ffi.hasTopBarTitle();
    return has;
  }

  async getTopBarOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getTopBarOverlay();
    return overlay;
  }

  async setTopBarOverlay(alpha: string): Promise<void> {
    await this._ffi.setTopBarOverlay(alpha);
    return;
  }

  async getTopBarHeight(): Promise<number> {
    const height = await this._ffi.getTopBarHeight();
    return height;
  }

  async getTopBarBackgroundColor(): Promise<Color.RGBAHex> {
    const color = await this._ffi.getTopBarBackgroundColor();
    return color;
  }

  async setTopBarBackgroundColor(color: string): Promise<void> {
    const colorHex = convertToRGBAHex(color);
    await this._ffi.setTopBarBackgroundColor(colorHex);

    return;
  }

  async getTopBarForegroundColor(): Promise<Color.RGBAHex> {
    const color = await this._ffi.getTopBarForegroundColor();
    return color;
  }

  async setTopBarForegroundColor(color: string): Promise<void> {
    const colorHex = convertToRGBAHex(color);
    await this._ffi.setTopBarForegroundColor(colorHex);
    return;
  }

  async getTopBarActions(): Promise<TopBar.TopBarItem[]> {
    this._actionList = await this._ffi.getTopBarActions();
    return this._actionList;
  }

  async setTopBarActions(): Promise<void> {
    await this._ffi.setTopBarActions(this._actionList);
    return;
  }

  async collectActions() {
    this._actionList = [];
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
    });

    await this.setTopBarActions();
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
      await this.setTopBarTitle(newVal as string);
    } else if (attrName === "background-color") {
      await this.setTopBarBackgroundColor(newVal as string);
    } else if (attrName === "foreground-color") {
      await this.setTopBarForegroundColor(newVal as string);
    } else if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        await this._ffi.setTopBarOverlay(newVal as string);
      }
    } else if (attrName === "hidden") {
      if (this.hasAttribute(attrName)) {
        await this._ffi.setTopBarHidden();
      }
    }
  }
}
