import { DwebPlugin } from "../native/dweb-plugin";
import { Plaoc, virtual_keyboard } from "./keyboard.d";

export class BfcsKeyboard extends DwebPlugin {
  private _ffi: Plaoc.VirtualKeyboardFFI = virtual_keyboard;
  private $element!: HTMLElement;

  constructor() {
    super();
    this.setAttribute("hidden", "");
  }

  connectedCallback() {
    if (this.hasAttribute("for") && this.getAttribute("for")?.length) {
      this.$element = document.querySelector("#" + this.getAttribute("for"))!;
      this.$element.dispatchEvent(new CustomEvent("focus"));
      this.$element.dispatchEvent(new CustomEvent("blur"));
      this.$element.addEventListener("focus", () => {
        this.removeAttribute("hidden");
      });
      this.$element.addEventListener("blur", () => {
        this.setAttribute("hidden", "");
      });
    }
  }

  disconnectedCallback() {
    this.$element.removeEventListener("focus", () => {});
    this.$element.removeEventListener("blur", () => {});
  }

  getSafeArea(): Promise<Plaoc.KeyboardSafeArea> {
    return new Promise<Plaoc.KeyboardSafeArea>((resolve, reject) => {
      const safeArea = JSON.parse(this._ffi.getSafeArea());

      resolve(safeArea);
    });
  }

  getHeight(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      const height = this._ffi.getHeight();

      resolve(height);
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

  show(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.show();

      resolve();
    });
  }

  /**
   * @Todo synchronized show
   */
  syncShow() {}

  hide(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.hide();

      resolve();
    });
  }

  /**
   * @Todo synchronized show
   */
  syncHide() {}

  /**
   * @Todo synchronized with the keyboard animation
   * @link https://developer.android.com/training/system-ui/sw-keyboard
   */

  static get observedAttributes() {
    return ["overlay", "hidden"];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "overlay") {
      if (this.hasAttribute(attrName)) {
        this._ffi.toggleOverlay(1);
      }
    } else if (attrName === "hidden") {
      if (this.hasAttribute(attrName)) {
        if (this.hasAttribute("sync")) {
          // synchronized
          this.syncHide();
        } else {
          // unsynchronized
          this.hide();
        }
      } else {
        if (this.hasAttribute("sync")) {
          // synchronized
          this.syncShow();
        } else {
          // unsynchronized
          this.show();
        }
      }
    }
  }
}
