/// <reference lib="dom" />

import { DwebPlugin } from "../native/dweb-plugin.ts";
export class BfcsTopBarButton extends DwebPlugin {
  constructor() {
    super();
  }

  connectedCallback() {
    this.setAttribute("bid", (Math.random() * Date.now()).toFixed(0));
  }

  disconnectedCallback() {
    this.removeEventListener("click", () => { });
  }

  static get observedAttributes() {
    return ["disabled"];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "disabled" && oldVal !== newVal) {
      if (this.hasAttribute(attrName)) {
        this.setAttribute(attrName, "");
      } else {
        this.removeAttribute(attrName);
      }
    }
  }
}
