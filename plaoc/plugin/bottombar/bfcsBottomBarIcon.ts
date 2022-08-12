import BfcsIcon from "./../icon";
import { DwebPlugin } from "../native/dweb-plugin";
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
