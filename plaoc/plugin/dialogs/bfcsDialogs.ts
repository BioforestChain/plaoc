import { DialogsFFI } from "./net";
import { DwebPlugin } from "../native/dweb-plugin";
import { Dialogs } from "./bfcsDialogs.type";

class BfcsDialogs extends DwebPlugin {
  protected _ffi: Dialogs.IDialogsFFI;

  constructor() {
    super();

    this._ffi = new DialogsFFI();
  }

  static get observedAttributes() {
    return ["visible"];
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) { }
}

export class BfcsDialogAlert extends BfcsDialogs {
  private _observer: MutationObserver;

  constructor() {
    super();

    this._observer = new MutationObserver(async (mutations) => {
      if (this.getAttribute("visible") === "true") {
        await this.openAlert();
      }
    });
  }

  connectedCallback() {
    // 自定义元素不允许在构造函数内设置属性或者创建子元素
    this.setAttribute("did", (Math.random() * Date.now()).toFixed(0));
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
      let alertConfig: Dialogs.IAlertConfig = {
        title: "",
        content: "",
        confirmText: "",
        dismissOnBackPress: true,
        dismissOnClickOutside: false,
      };

      alertConfig.title = this.getAttribute("title") ?? "";
      alertConfig.content = this.getAttribute("content") ?? "";
      // 是否可以通过按下后退按钮来关闭对话框
      alertConfig.dismissOnBackPress = this.hasAttribute("disOnBackPress")
        ? true
        : false;
      // 是否可以通过在对话框边界之外单击来关闭对话框。
      alertConfig.dismissOnClickOutside = this.hasAttribute("disOnClickOutside")
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
    if (attrName === "visible" && newVal === true) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "true");
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
      if (this.getAttribute("visible") === "true") {
        await this.openPrompt();
      }
    });
  }

  connectedCallback() {
    // 自定义元素不允许在构造函数内设置属性或者创建子元素
    this.setAttribute("did", (Math.random() * Date.now()).toFixed(0));
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
      let promptConfig: Dialogs.IPromptConfig = {
        title: "",
        label: "",
        confirmText: "",
        cancelText: "",
        defaultValue: "",
        dismissOnBackPress: true,
        dismissOnClickOutside: false,
      };

      promptConfig.title = this.getAttribute("title") ?? "";
      promptConfig.label = this.getAttribute("label") ?? "";
      promptConfig.defaultValue = this.getAttribute("defaultValue") ?? "";
      promptConfig.dismissOnBackPress = this.hasAttribute("disOnBackPress")
        ? true
        : false;
      promptConfig.dismissOnClickOutside = this.hasAttribute(
        "disOnClickOutside"
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
    if (attrName === "visible" && newVal === true) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "true");
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
      if (this.getAttribute("visible") === "true") {
        await this.openConfirm();
      }
    });
  }

  connectedCallback() {
    // 自定义元素不允许在构造函数内设置属性或者创建子元素
    this.setAttribute("did", (Math.random() * Date.now()).toFixed(0));
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
      let confirmConfig: Dialogs.IConfirmConfig = {
        title: "",
        message: "",
        confirmText: "",
        cancelText: "",
        dismissOnBackPress: true,
        dismissOnClickOutside: false,
      };

      confirmConfig.title = this.getAttribute("title") ?? "";
      confirmConfig.message = this.getAttribute("message") ?? "";
      confirmConfig.dismissOnBackPress = this.hasAttribute("disOnBackPress")
        ? true
        : false;
      confirmConfig.dismissOnClickOutside = this.hasAttribute(
        "disOnClickOutside"
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
    if (attrName === "visible" && newVal === true) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "true");
      } else {
        this.removeAttribute("visible");
      }
    }
  }
}

export class BfcsDialogWarning extends BfcsDialogs {
  private _observer: MutationObserver;

  constructor() {
    super();

    this._observer = new MutationObserver(async (mutations) => {
      if (this.getAttribute("visible") === "true") {
        await this.openWarning();
      }
    });
  }

  connectedCallback() {
    // 自定义元素不允许在构造函数内设置属性或者创建子元素
    this.setAttribute("did", (Math.random() * Date.now()).toFixed(0));
    this._observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
    });
  }

  disconnectedCallback() {
    this._observer.disconnect();
  }

  openWarning(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let confirmConfig: Dialogs.IConfirmConfig = {
        title: "",
        message: "",
        confirmText: "",
        cancelText: "",
        dismissOnBackPress: true,
        dismissOnClickOutside: false,
      };

      confirmConfig.title = this.getAttribute("title") ?? "";
      confirmConfig.message = this.getAttribute("message") ?? "";
      confirmConfig.dismissOnBackPress = this.hasAttribute("disOnBackPress")
        ? true
        : false;
      confirmConfig.dismissOnClickOutside = this.hasAttribute(
        "disOnClickOutside"
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
              confirmFunc = `document.querySelector('dweb-dialog-warning[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-warning[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          } else {
            if (index === 0) {
              confirmConfig.confirmText = childNode.getAttribute("label") ?? "";
              confirmFunc = `document.querySelector('dweb-dialog-warning[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            } else {
              confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
              cancelFunc = `document.querySelector('dweb-dialog-warning[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
            }
          }
        }
      );

      this._ffi.openWarning(confirmConfig, confirmFunc, cancelFunc);

      resolve();
    });
  }

  async attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {
    if (attrName === "visible" && newVal === true) {
      if (this.hasAttribute("visible")) {
        this.setAttribute("visible", "true");
      } else {
        this.removeAttribute("visible");
      }
    }
  }
}
