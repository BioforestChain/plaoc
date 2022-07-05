import { DialogsFFI } from "./ffi";

class BfcsDialogs extends HTMLElement {
  protected _ffi: Plaoc.IDialogsFFI;

  constructor() {
    super();

    this._ffi = new DialogsFFI();
    this.setAttribute("did", (Math.random() * Date.now()).toFixed(0));
  }

  connectedCallback() {}

  disconnectedCallback() {}

  static get observedAttributes() {
    return ["visible"];
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {}
}

export class BfcsDialogAlert extends BfcsDialogs {
  private _observer: MutationObserver;

  constructor() {
    super();

    this._observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openAlert();
      }
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

  openAlert(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let alertConfig: Plaoc.IAlertConfig = {
        title: "",
        content: "",
        confirmText: "",
        dismissOnBackPress: true,
        dismissOnClickOutSide: false,
      };

      alertConfig.title = this.getAttribute("title") ?? "";
      alertConfig.content = this.getAttribute("content") ?? "";
      alertConfig.dismissOnBackPress = this.hasAttribute("dismissOnBackPress")
        ? true
        : false;
      alertConfig.dismissOnClickOutSide = this.hasAttribute(
        "dismissOnClickOutSide"
      )
        ? true
        : false;

      const did = this.getAttribute("did");
      const childNode = this.querySelector("dweb-dialog-button");
      let bid: string = "";

      if (childNode) {
        alertConfig.confirmText = childNode.getAttribute("label") ?? "";
        bid = childNode.getAttribute("bid") ?? "";
      }

      const cb = `document.querySelector('dweb-dialog-alert[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;

      this._ffi.openAlert(alertConfig, cb);

      resolve();
    });
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {
    if (attrName === "visible" && oldVal !== newVal) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "");
      } else {
        this.removeAttribute("visible");
      }
    }
  }
}

export class BfcsDialogPrompt extends BfcsDialogs {
  private _observer: MutationObserver;

  constructor() {
    super();

    this._observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openPrompt();
      }
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

  openPrompt(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let promptConfig: Plaoc.IPromptConfig = {
        title: "",
        label: "",
        confirmText: "",
        cancelText: "",
        defaultValue: "",
        dismissOnBackPress: true,
        dismissOnClickOutSide: false,
      };

      promptConfig.title = this.getAttribute("title") ?? "";
      promptConfig.label = this.getAttribute("label") ?? "";
      promptConfig.defaultValue = this.getAttribute("defaultValue") ?? "";
      promptConfig.dismissOnBackPress = this.hasAttribute("dismissOnBackPress")
        ? true
        : false;
      promptConfig.dismissOnClickOutSide = this.hasAttribute(
        "dismissOnClickOutSide"
      )
        ? true
        : false;

      const did = this.getAttribute("did");
      let confirmFunc = "";
      let cancelFunc = "";

      this.querySelectorAll("dweb-dialog-button").forEach(
        (childNode, index) => {
          const bid = childNode.getAttribute("bid") ?? "";

          if (childNode.hasAttribute("aria-label")) {
            if (childNode.getAttribute("aria-label") === "confirm") {
              promptConfig.confirmText = childNode.getAttribute("label") ?? "";
              confirmFunc = `document.querySelector('dweb-dialog-prompt[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              promptConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-prompt[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          } else {
            if (index === 0) {
              promptConfig.confirmText = childNode.getAttribute("label") ?? "";
              confirmFunc = `document.querySelector('dweb-dialog-prompt[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              promptConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-prompt[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          }
        }
      );

      this._ffi.openPrompt(promptConfig, confirmFunc, cancelFunc);

      resolve();
    });
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {
    if (attrName === "visible" && oldVal !== newVal) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "");
      } else {
        this.removeAttribute("visible");
      }
    }
  }
}

export class BfcsDialogConfirm extends BfcsDialogs {
  private _observer: MutationObserver;

  constructor() {
    super();

    this._observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openConfirm();
      }
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

  openConfirm(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let confirmConfig: Plaoc.IConfirmConfig = {
        title: "",
        message: "",
        confirmText: "",
        cancelText: "",
        dismissOnBackPress: true,
        dismissOnClickOutSide: false,
      };

      confirmConfig.title = this.getAttribute("title") ?? "";
      confirmConfig.message = this.getAttribute("message") ?? "";
      confirmConfig.dismissOnBackPress = this.hasAttribute("dismissOnBackPress")
        ? true
        : false;
      confirmConfig.dismissOnClickOutSide = this.hasAttribute(
        "dismissOnClickOutSide"
      )
        ? true
        : false;

      const did = this.getAttribute("did");
      let confirmFunc = "";
      let cancelFunc = "";

      this.querySelectorAll("dweb-dialog-button").forEach(
        (childNode, index) => {
          const bid = childNode.getAttribute("bid") ?? "";

          if (childNode.hasAttribute("aria-label")) {
            if (childNode.getAttribute("aria-label") === "confirm") {
              confirmConfig.confirmText = childNode.getAttribute("label") ?? "";
              confirmFunc = `document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          } else {
            if (index === 0) {
              confirmConfig.confirmText = childNode.getAttribute("label") ?? "";
              confirmFunc = `document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          }
        }
      );

      this._ffi.openConfirm(confirmConfig, confirmFunc, cancelFunc);

      resolve();
    });
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {
    if (attrName === "visible" && oldVal !== newVal) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "");
      } else {
        this.removeAttribute("visible");
      }
    }
  }
}

export class BfcsDialogBeforeUnload extends BfcsDialogs {
  private _observer: MutationObserver;

  constructor() {
    super();

    this._observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openBeforeUnload();
      }
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

  openBeforeUnload(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let confirmConfig: Plaoc.IConfirmConfig = {
        title: "",
        message: "",
        confirmText: "",
        cancelText: "",
        dismissOnBackPress: true,
        dismissOnClickOutSide: false,
      };

      confirmConfig.title = this.getAttribute("title") ?? "";
      confirmConfig.message = this.getAttribute("message") ?? "";
      confirmConfig.dismissOnBackPress = this.hasAttribute("dismissOnBackPress")
        ? true
        : false;
      confirmConfig.dismissOnClickOutSide = this.hasAttribute(
        "dismissOnClickOutSide"
      )
        ? true
        : false;

      const did = this.getAttribute("did");
      let confirmFunc = "";
      let cancelFunc = "";

      this.querySelectorAll("dweb-dialog-button").forEach(
        (childNode, index) => {
          const bid = childNode.getAttribute("bid") ?? "";

          if (childNode.hasAttribute("aria-label")) {
            if (childNode.getAttribute("aria-label") === "confirm") {
              confirmConfig.confirmText = childNode.getAttribute("label") ?? "";
              confirmFunc = `document.querySelector('dweb-dialog-before-unload[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-before-unload[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          } else {
            if (index === 0) {
              confirmConfig.confirmText = childNode.getAttribute("label") ?? "";
              confirmFunc = `document.querySelector('dweb-dialog-before-unload[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-before-unload[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          }
        }
      );

      this._ffi.openBeforeUnload(confirmConfig, confirmFunc, cancelFunc);

      resolve();
    });
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {
    if (attrName === "visible" && oldVal !== newVal) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "");
      } else {
        this.removeAttribute("visible");
      }
    }
  }
}
