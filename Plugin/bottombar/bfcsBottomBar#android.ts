export class BfcsBottomBar extends HTMLElement {
  private _ffi: Bfcs.BottomBarFFI = bottom_bar;

  constructor() {
    super();
  }

  connectedCallback() {
    // this._ffi.toggleEnabled(1);
  }

  disconnectedCallback() {}

  toggleEnabled() {
    this._ffi.toggleEnabled(0);
  }

  getEnabled() {
    return this._ffi.getEnabled();
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

  setHeight(height: number) {
    this._ffi.setHeight(height);
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

  getActions(): Bfcs.BottomBarAction[] {
    return JSON.parse(this._ffi.getActions());
  }

  setActions(actionList: Bfcs.BottomBarAction[]) {
    this._ffi.setActions(JSON.stringify(actionList));
  }

  static get observedAttributes() {
    return [
      // "disabled",
      "backgroudColor",
      "foregroundColor",
      "overlay",
      // "action",
    ];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "backgroudColor") {
      this.setBackgroundColor(newVal as number);
    } else if (attrName === "foregroundColor") {
      this.setForegroundColor(newVal as number);
      // } else if (attrName === "action") {
      //   this.setActions(newVal as Bfcs.BottomBarAction[]);
    } else if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        this._ffi.toggleOverlay(1);
      }
      // } else if (attrName === "disabled") {
      //   if (this.hasAttribute(attrName)) {
      //     this.toggleEnabled();
      //   }
    }
  }
}
