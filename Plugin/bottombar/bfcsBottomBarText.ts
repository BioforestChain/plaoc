export class BfcsBottomBarText extends HTMLElement {
  constructor() {
    super();
  }

  static get observedAttributes() {
    return ["color", "selected-color"];
  }

  // attributeChangedCallback(
  //   attrName: string,
  //   oldVal: unknown,
  //   newVal: unknown
  // ) {}
}
