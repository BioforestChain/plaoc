import { BottomBarFFI } from "./ffi";

export class BfcsBottomBar extends HTMLElement {
  private _ffi: BottomBarFFI;
  private _observer: MutationObserver;
  private _actionList: Plaoc.BottomBarItem[] = [];

  constructor() {
    super();

    this._ffi = new BottomBarFFI();
    this._observer = new MutationObserver((mutations) => {
      this.collectActions();
    });
  }

  connectedCallback() {
    this._observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
    });
  }

  disconnectedCallback() {
    this._observer.disconnect();
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
    await this._ffi.setHeight(height);

    return;
  }

  async getBackgroundColor(): Promise<string> {
    const color = await this._ffi.getBackgroundColor();

    return color;
  }

  async setBackgroundColor(colorHex: string = "#ffffff"): Promise<void> {
    const colorObject: Plaoc.IColor = {
      color: colorHex,
      alpha:
        this.getAttribute("alpha") && this.getAttribute("alpha")!.length > 0
          ? parseFloat(this.getAttribute("alpha")!)
          : 0.5,
    };
    await this._ffi.setBackgroundColor(colorObject);

    return;
  }

  async getForegroundColor(): Promise<string> {
    const color = await this._ffi.getForegroundColor();

    return color;
  }

  async setForegroundColor(colorHex: string = "#ffffff"): Promise<void> {
    const colorObject: Plaoc.IColor = {
      color: colorHex,
      alpha:
        this.getAttribute("alpha") && this.getAttribute("alpha")!.length > 0
          ? parseFloat(this.getAttribute("alpha")!)
          : 0.5,
    };

    await this._ffi.setForegroundColor(colorObject);

    return;
  }

  async getActions(): Promise<Plaoc.BottomBarItem[]> {
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
      let icon: Plaoc.IPlaocIcon = {
        source: "",
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

      const bid = childNode.getAttribute("bid");
      const onClickCode = `document.querySelector('dweb-bottom-bar-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
      const disabled = childNode.hasAttribute("disabled") ? true : false;
      const selected = childNode.hasAttribute("selected") ? true : false;
      const selectable = childNode.hasAttribute("selectable") ? true : false;
      const label = childNode.getAttribute("label") ?? "";
      const colors =
        childNode.hasAttribute("colors") && childNode.getAttribute("colors")
          ? JSON.parse(childNode.getAttribute("colors")!)
          : undefined;

      this._actionList.push({
        icon,
        onClickCode,
        disabled,
        label,
        selectable,
        selected,
        colors,
      });
    });

    await this.setActions();
  }

  static get observedAttributes() {
    return [
      "disabled",
      "backgroudColor",
      "foregroundColor",
      "overlay",
      "height",
      "alpha",
    ];
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {
    if (attrName === "backgroudColor") {
      await this.setBackgroundColor(newVal as string);
    } else if (attrName === "foregroundColor") {
      await this.setForegroundColor(newVal as string);
    } else if (attrName === "height") {
      await this.setHeight(newVal as number);
    } else if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        await this._ffi.setOverlay();
      }
    } else if (attrName === "disabled") {
      if (this.hasAttribute(attrName)) {
        await this._ffi.setHidden();
      }
    }
  }
}
