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

  toggleEnabled(isEnabled: boolean) {
    const enabledInt = isEnabled ? 1 : 0;

    this._ffi.toggleEnabled(enabledInt);
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

  toggleOverlay(isOverlay: boolean) {
    const overlayInt = isOverlay ? 1 : 0;

    this._ffi.toggleOverlay(overlayInt);
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

  getActions(): string {
    return this._ffi.getActions();
  }

  setActions(action: string) {
    this._ffi.setActions(action);
  }

  static get observedAttributes() {
    return [
      "title",
      "disabled",
      "backgroudColor",
      "foregroundColor",
      "overlay",
    ];
  }

  attributeChangedCallback(
    attrName: string,
    oldVal: Bfcs.ValueType,
    newVal: Bfcs.ValueType
  ) {
    if (attrName === "title") {
      this.setTitle(newVal as string);
    } else if (attrName === "backgroudColor") {
      this.setBackgroundColor(newVal as number);
    } else if (attrName === "foregroundColor") {
      this.setForegroundColor(newVal as number);
    } else if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        this.toggleOverlay(true);
      } else {
        this.toggleOverlay(false);
      }
    } else if (attrName === "disabled") {
      if (this.hasAttribute(attrName)) {
        this.toggleEnabled(false);
      } else {
        this.toggleEnabled(true);
      }
    }
  }
}
