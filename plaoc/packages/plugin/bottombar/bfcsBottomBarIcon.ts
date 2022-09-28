import { DwebPlugin } from "../native/dweb-plugin.ts";
export class BfcsBottomBarIcon extends DwebPlugin {
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
