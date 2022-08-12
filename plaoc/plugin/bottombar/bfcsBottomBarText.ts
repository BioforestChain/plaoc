import { DwebPlugin } from "../native/dweb-plugin";
export class BfcsBottomBarText extends DwebPlugin {
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
