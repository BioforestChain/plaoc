export class BfcsTopBar extends HTMLElement {
  private _ffi: Bfcs.TopBarFFI = top_bar;

  constructor() {
    super();
  }

  connectedCallback() {}

  disconnectedCallback() {}

  back() {
    this._ffi.back();
  }

  toggleEnabled() {
    this._ffi.toggleEnabled(0);
  }

  getEnabled() {
    return this._ffi.getEnabled();
  }

  getTitle(): string {
    return this._ffi.getTitle();
  }

  setTitle(title: string) {
    this._ffi.setTitle(title);
  }

  hasTitle(): boolean {
    return this._ffi.hasTitle();
  }

  getOverlay(): boolean {
    return this._ffi.getOverlay();
  }

  toggleOverlay() {
    this._ffi.toggleOverlay(0);
  }

  getHeight(): number {
    return this._ffi.getHeight();
  }

  getBackgroundColor(): number {
    return this._ffi.getBackgroundColor();
  }

  setBackgroundColor(color: number) {
    this._ffi.setBackgroundColor(color);
  }

  getForegroundColor(): number {
    return this._ffi.getForegroundColor();
  }

  setForegroundColor(color: number) {
    this._ffi.setForegroundColor(color);
  }

  getActions(): Bfcs.TopBarAction[] {
    return JSON.parse(this._ffi.getActions());
  }

  setActions(actionList: Bfcs.TopBarAction[]) {
    this._ffi.setActions(JSON.stringify(actionList));
  }

  static get observedAttributes() {
    return [
      "title",
      "disabled",
      "backgroudColor",
      "foregroundColor",
      "overlay",
      // "action",
    ];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "title") {
      this.setTitle(newVal as string);
    } else if (attrName === "backgroudColor") {
      this.setBackgroundColor(newVal as number);
    } else if (attrName === "foregroundColor") {
      this.setForegroundColor(newVal as number);
      // } else if (attrName === "action") {
      //   this.setActions(newVal as Bfcs.TopBarAction[]);
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
