export class BfcsKeyboard extends HTMLElement {
  private _ffi: Bfcs.VirtualKeyboardFFI = virtual_keyboard;

  constructor() {
    super();
  }

  connectedCallback() {}

  disconnectedCallback() {}

  getSafeArea(): Bfcs.KeyboardSafeArea {
    return JSON.parse(this._ffi.getSafeArea());
  }

  getHeight(): number {
    return this._ffi.getHeight();
  }

  getOverlay(): boolean {
    return this._ffi.getOverlay();
  }

  toggleOverlay(): void {
    this._ffi.toggleOverlay(0);
  }

  show(): void {
    this._ffi.show();
  }

  hide(): void {
    this._ffi.hide();
  }

  static get observedAttributes() {
    return ["overlay"];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        this._ffi.toggleOverlay(1);
      }
    }
  }
}
