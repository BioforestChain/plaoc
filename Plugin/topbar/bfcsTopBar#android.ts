export class BfcsTopBar extends HTMLElement {
  private _ffi: Plaoc.TopBarFFI = top_bar;
  private _actionList: Plaoc.TopBarAction[] = [];

  constructor() {
    super();
  }

  connectedCallback() {
    let observer = new MutationObserver((mutations) => {
      // mutations.forEach((mutation) => {
      //   // alert(mutation.oldValue);
      // });
      // alert("observe");
      this.collectActions();
    });

    observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
      attributeFilter: [
        "disabled",
        "icon",
        "type",
        "description",
        "size",
        "source",
      ],
    });

    this.collectActions();
  }

  disconnectedCallback() {}

  back(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.back();
      resolve();
    });
  }

  toggleEnabled() {
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

  getTitle(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const title = this._ffi.getTitle();
      resolve(title);
    });
  }

  setTitle(title: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setTitle(title);
      resolve();
    });
  }

  hasTitle(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const hasTitle = this._ffi.hasTitle();

      resolve(hasTitle);
    });
  }

  getOverlay(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const isOverlay = this._ffi.getOverlay();

      resolve(isOverlay);
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

  getColorInt(color: string = "#ffffff", opacity: number = 0.5) {
    return parseInt(color.slice(1), 16) + ((opacity * 255) << (8 * 3));
  }

  getBackgroundColor(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const color = this._ffi.getBackgroundColor();
      const colorHex = "#" + color.toString(16).slice(2);

      resolve(colorHex);
    });
  }

  setBackgroundColor(colorHex: string = "#ffffff"): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const opacity = this.getAttribute("opacity")
        ? parseFloat(this.getAttribute("opacity")!)
        : 0.5;
      const color = this.getColorInt(colorHex, opacity);
      this._ffi.setBackgroundColor(color);

      resolve();
    });
  }

  getForegroundColor(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const color = this._ffi.getForegroundColor();
      const colorHex = "#" + color.toString(16).slice(2);

      resolve(colorHex);
    });
  }

  setForegroundColor(colorHex: string = "#ffffff"): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const opacity = this.getAttribute("opacity")
        ? parseFloat(this.getAttribute("opacity")!)
        : 0.5;
      const color = this.getColorInt(colorHex, opacity);

      this._ffi.setForegroundColor(color);

      resolve();
    });
  }

  // async getActions(): Promise<Plaoc.TopBarAction[]> {
  //   return new Promise<Plaoc.TopBarAction[]>((resolve, reject) => {
  //     const actionList = JSON.parse(this._ffi.getActions());

  //     resolve(actionList);
  //   });
  // }
  getActions(): Promise<Plaoc.TopBarAction[]> {
    return new Promise<Plaoc.TopBarAction[]>((resolve, reject) => {
      this._actionList = JSON.parse(this._ffi.getActions());

      resolve(this._actionList);
    });
  }

  // async setActions(actionList: Plaoc.TopBarAction[]) {
  //   this._ffi.setActions(JSON.stringify(actionList));
  // }
  // async setActions(): Promise<void> {
  //   return new Promise<void>((resolve, reject) => {
  //     this._ffi.setActions(JSON.stringify(this._actionList));

  //     resolve();
  //   });
  // }
  setActions(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.setActions(JSON.stringify(this._actionList));

      resolve();
    });
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
      "disabled",
      "backgroudColor",
      "foregroundColor",
      "overlay",
      "opacity",
    ];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "title") {
      this.setTitle(newVal as string);
    } else if (attrName === "backgroudColor") {
      this.setBackgroundColor(newVal as string);
    } else if (attrName === "foregroundColor") {
      this.setForegroundColor(newVal as string);
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
