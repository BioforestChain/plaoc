/// <reference lib="dom" />

import { convertToRGBAHex } from "../util/index.ts";
import { DwebPlugin } from "../native/dweb-plugin.ts";
import { BottomBarNet } from "./net.ts";
import { BottomBar } from "./bfcsBottomBarType.ts";
import { Icon } from "../icon/bfspIconType.ts";
import { Color } from "../types/colorType.ts";

export class BfcsBottomBar extends DwebPlugin {
  private net: BottomBarNet;
  private _observer: MutationObserver;
  private _actionList: BottomBar.BottomBarItem[] = [];

  constructor() {
    super();

    this.net = new BottomBarNet();
    this._observer = new MutationObserver(async () => {
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
        "diSelectable", // 不允许选择，不加diSelectable属性则允许选择
        "label",
        // "colors",
        "type",
        "description",
        "size",
        "source",
        "color",
        "selected-color",
        "indicator-color",
        "height",
        "hide-value",
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
    } else {
      await this.collectActions();
    }
  }
  /**
   * 是否隐藏bottomBar
   * @param isEnabled true隐藏
   * @returns
   */
  async setHidden(isEnabled = true): Promise<boolean> {
    return await this.net.setHidden(isEnabled);
  }
  /**获取bottomBar是否隐藏 */
  async getHidden(): Promise<boolean> {
    const isEnabled = await this.net.getHidden();

    return isEnabled;
  }
  /**获取bottomBar透明度 */
  async getOverlay(): Promise<number> {
    return await this.net.getBottomBarAlpha();
  }
  /**
   * 设置bottomBar是否透明
   * @param alpha 默认1不透明
   * @returns
   */
  async setOverlay(alpha = "1"): Promise<number> {
    return await this.net.setBottomBarAlpha(alpha);
  }
  /**获取bottomBar高度 */
  async getHeight(): Promise<number> {
    const height = await this.net.getHeight();
    return height;
  }
  /**
   * 设置bottomBar高度
   * @param height
   * @returns
   */
  async setHeight(height: number): Promise<boolean> {
    this.setAttribute("height", `${height}`);
    return await this.net.setHeight(height);
  }
  /**获取bottomBar背景颜色 */
  async getBackgroundColor(): Promise<Color.RGBAHex> {
    const color = await this.net.getBackgroundColor();

    return color;
  }
  /**
   * 设置bottomBar背景颜色
   * @param color
   * @returns
   */
  async setBackgroundColor(color: string): Promise<boolean> {
    const colorHex = convertToRGBAHex(color);
    return await this.net.setBackgroundColor(colorHex);
  }

  async getForegroundColor(): Promise<Color.RGBAHex> {
    const color = await this.net.getForegroundColor();

    return color;
  }

  async setForegroundColor(color: string): Promise<boolean> {
    const colorHex = convertToRGBAHex(color);
    return await this.net.setForegroundColor(colorHex);
  }

  async getActions(): Promise<BottomBar.BottomBarItem[]> {
    this._actionList = await this.net.getActions();

    return this._actionList;
  }

  async setActions(): Promise<void> {
    await this.net.setActions(this._actionList);

    return;
  }

  async collectActions() {
    this._actionList = [];
    this.querySelectorAll("dweb-bottom-bar-button").forEach((childNode) => {
      const icon: Icon.IPlaocIcon = {
        source: "",
        type: "NamedIcon" as Icon.IconType.NamedIcon,
      };

      const colors: BottomBar.IBottomBarColors = {};
      let label = "";
      let alwaysShowLabel = false;

      if (childNode.querySelector("dweb-bottom-bar-icon")) {
        const $ = childNode.querySelector("dweb-bottom-bar-icon")!;

        icon.source = $.getAttribute("source") ?? "";
        icon.type = $.hasAttribute("type")
          ? ($.getAttribute("type") as Icon.IconType)
          : ("NamedIcon" as Icon.IconType.NamedIcon);
        icon.description = $.getAttribute("description") ?? "";
        icon.size = $.hasAttribute("size")
          ? ($.getAttribute("size") as unknown as number)
          : undefined;

        if ($.hasAttribute("color")) {
          colors.iconColor = convertToRGBAHex($.getAttribute("color")!);
        }
        if ($.hasAttribute("selected-color")) {
          colors.iconColorSelected = convertToRGBAHex(
            $.getAttribute("selected-color")!
          );
        }
      }

      if (childNode.querySelector("dweb-bottom-bar-text")) {
        const $ = childNode.querySelector("dweb-bottom-bar-text")!;

        if ($.hasAttribute("value")) {
          label = $.getAttribute("value")!;
        }
        // 是否一直显示文字，如果指定此值，则只有选择才会显示文字
        alwaysShowLabel = $.hasAttribute("hide-value") ? true : false;

        if ($.hasAttribute("color")) {
          colors.textColor = convertToRGBAHex($?.getAttribute("color")!);
        }
        if ($.hasAttribute("selected-color")) {
          colors.textColorSelected = convertToRGBAHex(
            $.getAttribute("selected-color")!
          );
        }
      }

      const bid = childNode.getAttribute("bid");
      const onClickCode = `document.querySelector('dweb-bottom-bar-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
      // 禁止触发所有事件，包括无障碍事件
      const disabled = childNode.hasAttribute("disabled") ? true : false;
      const selected = childNode.hasAttribute("selected") ? true : false;
      // 不允许选择，不加diSelectable属性则允许选择
      const diSelectable = childNode.hasAttribute("diSelectable")
        ? false
        : true;
      // 指示器颜色，当前选中的背景颜色
      if (childNode.hasAttribute("indicator-color")) {
        colors.indicatorColor = convertToRGBAHex(
          childNode.getAttribute("indicator-color")!
        );
      }
      this._actionList.push({
        icon,
        onClickCode,
        alwaysShowLabel,
        disabled,
        label,
        selectable: diSelectable,
        selected,
        colors: Object.keys(colors).length === 0 ? undefined : colors,
      });
    });

    await this.setActions();
  }

  static get observedAttributes() {
    return [
      "hidden", // 控制是否显示
      "background-color", // 背景色
      "foreground-color", // 前景色
      "overlay", // 是否开启bottombar遮罩。
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
        await this.setOverlay(newVal as string);
      }
    } else if (attrName === "hidden") {
      if (this.hasAttribute(attrName)) {
        await this.setHidden();
      }
    }
  }
}
