/// <reference lib="dom" />

import { DwebPlugin } from "../native/dweb-plugin.ts";
export class BfcsBottomBarButton extends DwebPlugin {
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
    return ["disabled", "selected", "diSelectable", "indicator-color"];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    if (attrName === "disabled" && oldVal !== newVal) {
      if (this.hasAttribute(attrName)) {
        this.setAttribute(attrName, "");
      } else {
        this.removeAttribute(attrName);
      }
    } else if (attrName === "selected" && oldVal !== newVal) {
      if (this.hasAttribute("selected")) {
        this.setAttribute(attrName, "");
      } else {
        this.removeAttribute(attrName);
      }
    } else if (attrName === "diSelectable" && oldVal !== newVal) {
      if (this.hasAttribute("diSelectable")) {
        this.setAttribute(attrName, "");
      } else {
        this.removeAttribute(attrName);
      }
    }
  }
}
