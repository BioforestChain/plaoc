import { DwebPlugin } from "../native/dweb-plugin.ts";
export class BfcsBottomBarText extends DwebPlugin {
  constructor() {
    super();
  }

  static get observedAttributes() {
    return ["value", "color", "selected-color", "hide-value"];
  }

  // attributeChangedCallback(
  //   attrName: string,
  //   oldVal: unknown,
  //   newVal: unknown
  // ) {}
}
