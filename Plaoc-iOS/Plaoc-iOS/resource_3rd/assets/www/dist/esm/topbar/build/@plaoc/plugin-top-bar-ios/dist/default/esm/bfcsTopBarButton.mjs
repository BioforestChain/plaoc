class t extends HTMLElement {
  constructor() {
    super();
  }
  connectedCallback() {
    this.setAttribute("bid", (Math.random() * Date.now()).toFixed(0));
  }
  disconnectedCallback() {
    this.removeEventListener("click", () => {
    });
  }
  static get observedAttributes() {
    return ["disabled"];
  }
  attributeChangedCallback(t2, e, s) {
    t2 === "disabled" && e !== s && (this.hasAttribute(t2) ? this.setAttribute(t2, "") : this.removeAttribute(t2));
  }
}
export { t as BfcsTopBarButton };
//# sourceMappingURL=bfcsTopBarButton.mjs.map
