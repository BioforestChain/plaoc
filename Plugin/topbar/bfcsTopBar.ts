import { TopBarFFI } from "./ffi";

export class BfcsTopBar extends HTMLElement {
  private _ffi: Plaoc.ITopBarFFI;
  private _observer: MutationObserver;
  private _actionList: Plaoc.TopBarItem[] = [];
  private _alpha: Plaoc.AlphaValueHex = "ff";

  constructor() {
    super();

    this._ffi = new TopBarFFI();
    this._observer = new MutationObserver((mutations) => {
      this.collectActions();
    });

    // this.setAlpha();
    this._init();
  }

  connectedCallback() {
    this._observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
      // attributeFilter: [
      //   "disabled",
      //   "icon",
      //   "type",
      //   "description",
      //   "size",
      //   "source",
      // ],
    });
  }

  disconnectedCallback() {
    this._observer.disconnect();
  }

  private async _init() {
    this._setAlpha();

    const height = await this.getHeight();
    this.setAttribute("height", `${height}`);
  }

  private _setAlpha() {
    if (
      this.hasAttribute("alpha") &&
      this.getAttribute("alpha") &&
      this.getAttribute("alpha")!.length > 0
    ) {
      try {
        const alphaFloat = parseFloat(this.getAttribute("alpha")!);
        const alpha = Math.round(alphaFloat * 255).toString(16);

        if (alpha.length > 1) {
          this._alpha = alpha as Plaoc.AlphaValueHex;
        } else {
          this._alpha = ("0" + alpha) as Plaoc.AlphaValueHex;
        }
      } catch {}
    }
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

  async getBackgroundColor(): Promise<string> {
    const color = await this._ffi.getBackgroundColor(this._alpha);

    return color;
  }

  async setBackgroundColor(color: Plaoc.RGB): Promise<void> {
    await this._ffi.setBackgroundColor(color, this._alpha);

    return;
  }

  async getForegroundColor(): Promise<string> {
    const color = await this._ffi.getForegroundColor(this._alpha);

    return color;
  }

  async setForegroundColor(color: Plaoc.RGB): Promise<void> {
    await this._ffi.setForegroundColor(color, this._alpha);

    return;
  }

  async getActions(): Promise<Plaoc.TopBarItem[]> {
    this._actionList = await this._ffi.getActions();

    return this._actionList;
  }

  async setActions(): Promise<void> {
    await this._ffi.setActions(this._actionList);

    return;
  }

  async collectActions() {
    this._actionList = [];

    this.querySelectorAll("dweb-top-bar-button").forEach((childNode) => {
      let icon: Plaoc.IPlaocIcon = {
        source: "",
        // type: Plaoc.IconType.NamedIcon,
        type: "NamedIcon" as Plaoc.IconType.NamedIcon,
      };

      if (childNode.querySelector("dweb-icon")) {
        let $ = childNode.querySelector("dweb-icon");

        icon.source = $?.getAttribute("source") ?? "";
        icon.type = $?.hasAttribute("type")
          ? ($.getAttribute("type") as Plaoc.IconType)
          : ("NamedIcon" as Plaoc.IconType.NamedIcon);
        icon.description = $?.getAttribute("description") ?? "";
        icon.size = $?.hasAttribute("size")
          ? ($.getAttribute("size") as unknown as number)
          : undefined;
      }
      // const icon = JSON.parse(
      //   childNode.getAttribute("icon")! as Plaoc.IconType
      // );

      const bid = childNode.getAttribute("bid");
      const onClickCode = `document.querySelector('dweb-top-bar-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
      const disabled = childNode.hasAttribute("disabled") ? true : false;
      this._actionList.push({ icon, onClickCode, disabled });
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
