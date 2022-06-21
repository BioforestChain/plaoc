// export class BfcsDialogs extends HTMLElement {
//   private _ffi: Plaoc.DialogsFFI = native_dialog;

//   constructor() {
//     super();
//   }

//   openAlert(config: Plaoc.AlertConfig, cb: string): void {
//     this._ffi.openAlert(JSON.stringify(config), cb);
//   }
//   openPrompt(config: Plaoc.PromptConfig, cb: string): void {
//     this._ffi.openPrompt(JSON.stringify(config), cb);
//   }
//   openConfirm(config: Plaoc.ConfirmConfig, cb: string): void {
//     this._ffi.openConfirm(JSON.stringify(config), cb);
//   }
//   openBeforeUnload(config: Plaoc.ConfirmConfig, cb: string): void {
//     this._ffi.openBeforeUnload(JSON.stringify(config), cb);
//   }
// }

class BfcsDialogs extends HTMLElement {
  protected _ffi: Plaoc.DialogsFFI = native_dialog;

  constructor() {
    super();
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
  constructor() {
    super();
  }

  connectedCallback() {
    let observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openAlert();
      }
    });

    observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
    });
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

      this._ffi.openAlert(JSON.stringify(alertConfig), cb);

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
  constructor() {
    super();
  }

  connectedCallback() {
    let observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openPrompt();
      }
    });

    observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
    });
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
      let cb: string = "";

      this.querySelectorAll("dweb-dialog-button").forEach(
        (childNode, index) => {
          const bid = childNode.getAttribute("bid") ?? "";
          if (index === 0) {
            promptConfig.confirmText = childNode.getAttribute("label") ?? "";
            cb += `document.querySelector('dweb-dialog-prompt[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
          } else {
            promptConfig.cancelText = childNode.getAttribute("label") ?? "";
            cb += `;document.querySelector('dweb-dialog-prompt[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
          }
        }
      );

      this._ffi.openPrompt(JSON.stringify(promptConfig), cb);

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
  constructor() {
    super();
  }

  connectedCallback() {
    let observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openConfirm();
      }
    });

    observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
    });
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
      let cb: string = "";

      this.querySelectorAll("dweb-dialog-button").forEach(
        (childNode, index) => {
          const bid = childNode.getAttribute("bid") ?? "";

          if (index === 0) {
            confirmConfig.confirmText = childNode.getAttribute("label") ?? "";
            cb += `document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
          } else {
            confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
            cb += `;document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
          }
        }
      );

      this._ffi.openConfirm(JSON.stringify(confirmConfig), cb);

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
  constructor() {
    super();
  }

  connectedCallback() {
    let observer = new MutationObserver(async (mutations) => {
      if (this.hasAttribute("visible")) {
        await this.openBeforeUnload();
      }
    });

    observer.observe(this, {
      subtree: true,
      childList: true,
      attributes: true,
    });
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
      let cb: string = "";

      this.querySelectorAll("dweb-dialog-button").forEach(
        (childNode, index) => {
          const bid = childNode.getAttribute("bid") ?? "";

          if (index === 0) {
            confirmConfig.confirmText = childNode.getAttribute("label") ?? "";
            cb += `document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
          } else {
            confirmConfig.cancelText = childNode.getAttribute("label") ?? "";
            cb += `;document.querySelector('dweb-dialog-confirm[did="${did}"] dweb-dialog-button[bid="${bid}"]').dispatchEvent(new CustomEvent('click'))`;
          }
        }
      );

      this._ffi.openBeforeUnload(JSON.stringify(confirmConfig), cb);

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
