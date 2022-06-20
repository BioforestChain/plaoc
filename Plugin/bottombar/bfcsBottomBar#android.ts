export class BfcsBottomBar extends HTMLElement {
  private _ffi: Plaoc.BottomBarFFI = bottom_bar;
  private _actionList: Plaoc.BottomBarAction[] = [];

  constructor() {
    super();
  }

  connectedCallback() {
    let observer = new MutationObserver((mutations) => {
      this.collectActions();
    });

    observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
    });

    this.collectActions();
  }

  disconnectedCallback() {}

  toggleEnabled(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleEnabled(0);
      resolve();
    });
  }

  getEnabled(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const isEnabled = this._ffi.getEnabled();

      resolve(isEnabled);
    });
  }

  getOverlay(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const overlay = this._ffi.getOverlay();

      resolve(overlay);
    });
  }

  toggleOverlay(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.toggleOverlay(0);

      resolve();
    });
  }

  getHeight(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const height = this._ffi.getHeight();

      resolve(height);
    });
  }

  setHeight(height: number): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setHeight(height);

      resolve();
    });
  }

  getBackgroundColor(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const colorInt = this._ffi.getBackgroundColor();

      resolve(colorInt);
    });
  }

  setBackgroundColor(color: number): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setBackgroundColor(color);

      resolve();
    });
  }

  getForegroundColor(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const colorInt = this._ffi.getForegroundColor();

      resolve(colorInt);
    });
  }

  setForegroundColor(color: number): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setForegroundColor(color);

      resolve();
    });
  }

  // async getActions(): Promise<Plaoc.BottomBarAction[]> {
  //   return JSON.parse(this._ffi.getActions());
  // }

  getActions(): Promise<Plaoc.BottomBarAction[]> {
    return new Promise<Plaoc.BottomBarAction[]>((resolve, reject) => {
      this._actionList = JSON.parse(this._ffi.getActions());

      resolve(this._actionList);
    });
  }

  // async setActions(actionList: Plaoc.BottomBarAction[]) {
  //   this._ffi.setActions(JSON.stringify(actionList));
  // }

  setActions(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setActions(JSON.stringify(this._actionList));

      resolve();
    });
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
    ];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "backgroudColor") {
      this.setBackgroundColor(newVal as number);
    } else if (attrName === "foregroundColor") {
      this.setForegroundColor(newVal as number);
    } else if (attrName === "height") {
      this.setHeight(newVal as number);
    } else if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        this._ffi.toggleOverlay(1);
      }
    } else if (attrName === "disabled") {
      if (this.hasAttribute(attrName)) {
        this._ffi.toggleEnabled(1);
      }
    }
  }
}
