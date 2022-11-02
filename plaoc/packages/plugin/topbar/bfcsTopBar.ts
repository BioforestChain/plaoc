import { TopBarNet } from "./net.ts";
import { TopBar } from "./bfcsTopBarType.ts";
import { Icon } from "../icon/bfspIconType.ts";
import { Color } from "../types/colorType.ts";
import { DwebPlugin } from "../native/dweb-plugin.ts";
import { convertToRGBAHex } from "../util/index.ts";

export class BfcsTopBar extends DwebPlugin {
  private net: TopBar.ITopBarNet;
  private _observer: MutationObserver;
  private _actionList: TopBar.TopBarItem[] = [];

  constructor() {
    super();

    this.net = new TopBarNet();
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
  /**返回上一级 */
  async topBarNavigationBack(): Promise<boolean> {
    return await this.net.topBarNavigationBack();
  }
  /**
   * 设置是否隐藏
   * @param isEnabled boolean
   * @returns
   */
  async setTopBarShow(isShow: boolean): Promise<boolean> {
    return await this.net.setTopBarShow(isShow);
  }
  /**获取是否隐藏的状态 */
  async getTopBarShow(): Promise<boolean> {
    const isEnabled = await this.net.getTopBarShow();
    return isEnabled;
  }
  /**获取标题 */
  async getTopBarTitle(): Promise<string> {
    const title = await this.net.getTopBarTitle();
    return title;
  }
  /**
   * 设置状态栏标题
   * @param title string
   * @returns
   */
  async setTopBarTitle(title: string): Promise<boolean> {
    return await this.net.setTopBarTitle(title);
  }
  /**查看是否设置了标题 */
  async hasTopBarTitle(): Promise<boolean> {
    const has = await this.net.hasTopBarTitle();
    return has;
  }
  /**
   * 获取状态栏的透明度
   * @returns number
   */
  async getTopBarAlpha(): Promise<number> {
    const alpha = await this.net.getTopBarAlpha();
    return alpha;
  }
  /**
   * 设置状态栏的透明度
   * @param alpha
   * @returns
   */
  async setTopBarAlpha(alpha: string): Promise<boolean> {
    return await this.net.setTopBarAlpha(alpha);
  }
  /**获取状态栏高度 */
  async getTopBarHeight(): Promise<number> {
    const height = await this.net.getTopBarHeight();
    return height;
  }
  /**获取状态栏背景颜色 */
  async getTopBarBackgroundColor(): Promise<Color.RGBAHex> {
    const color = await this.net.getTopBarBackgroundColor();
    return color;
  }
  /**
   * 设置状态栏背景颜色
   * @param color
   * @returns
   */
  async setTopBarBackgroundColor(color: string): Promise<boolean> {
    const colorHex = convertToRGBAHex(color);
    return await this.net.setTopBarBackgroundColor(colorHex);
  }
  /**获取状态栏文字和图标颜色 */
  async getTopBarForegroundColor(): Promise<Color.RGBAHex> {
    const color = await this.net.getTopBarForegroundColor();
    return color;
  }
  /**
   * 设置状态栏文字和图标颜色
   * @param color
   * @returns
   */
  async setTopBarForegroundColor(color: string): Promise<boolean> {
    const colorHex = convertToRGBAHex(color);
    return await this.net.setTopBarForegroundColor(colorHex);
  }

  async getTopBarActions(): Promise<TopBar.TopBarItem[]> {
    this._actionList = await this.net.getTopBarActions();
    return this._actionList;
  }

  async setTopBarActions(): Promise<void> {
    await this.net.setTopBarActions(this._actionList);
    return;
  }

  async collectActions() {
    this._actionList = [];
    this.querySelectorAll("dweb-top-bar-button").forEach((childNode) => {
      const icon: Icon.IPlaocIcon = {
        source: "",
        // type: TopBar.IconType.NamedIcon,
        type: "NamedIcon" as Icon.IconType.NamedIcon,
      };

      if (childNode.querySelector("dweb-icon")) {
        const $ = childNode.querySelector("dweb-icon");

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
      this._actionList.push({ icon, onClickCode: onClickCode, disabled });
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
        await this.net.setTopBarAlpha(newVal as string);
      }
    } else if (attrName === "hidden") {
      if (this.hasAttribute(attrName)) {
        await this.net.setTopBarHidden();
      }
    }
  }
}
