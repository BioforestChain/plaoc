export class BfcsDialogButton extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.setAttribute("bid", (Math.random() * Date.now()).toFixed(0));

    // https://developer.mozilla.org/zh-CN/docs/Web/Accessibility/ARIA/Attributes/aria-label
    this.setAttribute("label", this.textContent ?? "");
    this.textContent = "";
  }

  disconnectedCallback() {
    this.removeEventListener("click", () => {});
  }
}
