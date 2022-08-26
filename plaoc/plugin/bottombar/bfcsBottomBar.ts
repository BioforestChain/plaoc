import { convertToRGBAHex } from "./../util";
import { DwebPlugin } from "../native/dweb-plugin";
import { BottomBarFFI } from "./ffi";
import "../typings";
import { BottomBar } from "./bfcsBottomBar.type";
import { Icon } from "../icon/bfspIcon.type";
import { Color } from "../typings/types/color.type";

export class BfcsBottomBar extends DwebPlugin {
  private _ffi: BottomBarFFI;
  private _observer: MutationObserver;
  private _actionList: BottomBar.BottomBarItem[] = [];

  constructor() {
    super();

    this._ffi = new BottomBarFFI();
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
        "selected",
        "selectable",
        "label",
        // "colors",
        "type",
        "description",
        "size",
        "source",
        "color",
        "selected-color",
        "indicator-color",
      ],
    });

    this._init();
  }

  disconnectedCallback() {
    this._observer.disconnect();
  }

  private async _init() {
    const height = await this.getHeight();

    if (height) {
      this.setAttribute("height", `${height}`);
    }
  }

  async toggleEnabled(): Promise<void> {
    await this._ffi.toggleEnabled();

    return;
  }

  async getEnabled(): Promise<boolean> {
    const isEnabled = await this._ffi.getEnabled();

    return isEnabled;
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

  async setHeight(height: number): Promise<void> {
    this.setAttribute("height", `${height}`);

    await this._ffi.setHeight(height);

    return;
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

  async getActions(): Promise<BottomBar.BottomBarItem[]> {
    this._actionList = await this._ffi.getActions();

    return this._actionList;
  }

  async setActions(): Promise<void> {
    await this._ffi.setActions(this._actionList);

    return;
  }

  async collectActions() {
    this._actionList = [];

    this.querySelectorAll("dweb-bottom-bar-button").forEach((childNode) => {
      let icon: Icon.IPlaocIcon = {
        source: "",
        type: "NamedIcon" as Icon.IconType.NamedIcon,
      };

      let colors: BottomBar.IBottomBarColors = {};
      let label: string = "";

      if (childNode.querySelector("dweb-bottom-bar-icon")) {
        let $ = childNode.querySelector("dweb-bottom-bar-icon");

        icon.source = $?.getAttribute("source") ?? "";
        icon.type = $?.hasAttribute("type")
          ? ($.getAttribute("type") as Icon.IconType)
          : ("NamedIcon" as Icon.IconType.NamedIcon);
        icon.description = $?.getAttribute("description") ?? "";
        icon.size = $?.hasAttribute("size")
          ? (($.getAttribute("size") as unknown) as number)
          : undefined;

        if ($?.hasAttribute("color")) {
          colors.iconColor = convertToRGBAHex($?.getAttribute("color")!);
        }
        if ($?.hasAttribute("selected-color")) {
          colors.iconColorSelected = convertToRGBAHex(
            $?.getAttribute("selected-color")!
          );
        }
      }

      if (childNode.querySelector("dweb-bottom-bar-text")) {
        let $ = childNode.querySelector("dweb-bottom-bar-text");

        label = $?.textContent ?? "";
        if ($?.hasAttribute("color")) {
          colors.textColor = convertToRGBAHex($?.getAttribute("color")!);
        }
        if ($?.hasAttribute("selected-color")) {
          colors.textColorSelected = convertToRGBAHex(
            $?.getAttribute("selected-color")!
          );
        }
      }

      const bid = childNode.getAttribute("bid");
      const onClickCode = `document.querySelector('dweb-bottom-bar-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
      const disabled = childNode.hasAttribute("disabled") ? true : false;
      const selected = childNode.hasAttribute("selected") ? true : false;
      const selectable = childNode.hasAttribute("selectable") ? true : false;

      if (childNode.hasAttribute("indicator-color")) {
        colors.indicatorColor = convertToRGBAHex(
          childNode.getAttribute("indicator-color")!
        );
      }

      this._actionList.push({
        icon,
        onClickCode,
        disabled,
        label,
        selectable,
        selected,
        colors: JSON.stringify(colors) === "{}" ? undefined : colors,
      });
    });

    await this.setActions();
  }

  static get observedAttributes() {
    return [
      "hidden",
      "background-color",
      "foreground-color",
      "overlay",
      "height",
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

    if (attrName === "background-color") {
      await this.setBackgroundColor(newVal as string);
    } else if (attrName === "foreground-color") {
      await this.setForegroundColor(newVal as string);
    } else if (attrName === "height") {
      await this.setHeight(newVal as number);
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
