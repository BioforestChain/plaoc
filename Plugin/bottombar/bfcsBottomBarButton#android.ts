export class BfcsBottomBarButton extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.setAttribute("bid", (Math.random() * Date.now()).toFixed(0));
  }

  disconnectedCallback() {
    this.removeEventListener("click", () => {});
  }

  static get observedAttributes() {
    return ["disabled", "selected", "selectable", "label", "color"];
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
    } else if (attrName === "selectable" && oldVal !== newVal) {
      if (this.hasAttribute("selectable")) {
        this.setAttribute(attrName, "");
      } else {
        this.removeAttribute(attrName);
      }
    }
  }
}
