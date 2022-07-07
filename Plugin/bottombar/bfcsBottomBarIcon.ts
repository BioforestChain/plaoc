import BfcsIcon from "@plaoc/plugin-icon";

export class BfcsBottomBarIcon extends BfcsIcon {
  constructor() {
    super();
  }

  static get observedAttributes() {
    return ["type", "description", "size", "source", "color", "selected-color"];
  }

  // attributeChangedCallback(
  //   attrName: string,
  //   oldVal: unknown,
  //   newVal: unknown
  // ) {}
}
