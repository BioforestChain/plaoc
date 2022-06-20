export class BfcsStatusBarColor extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {}

  disconnectedCallback() {}

  static get observedAttributes() {
    return ["color", "darkicons", "opacity"];
  }

  attributeChangedCallback(
    attrName: string,
    oldVal: unknown,
    newVal: unknown
  ) {}
}
