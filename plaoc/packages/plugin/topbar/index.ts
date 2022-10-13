/// <reference lib="dom" />

import { BfcsTopBar } from "./bfcsTopBar.ts";
import { BfcsTopBarButton } from "./bfcsTopBarButton.ts";

export {
  BfcsTopBar
}

customElements.define("dweb-top-bar", BfcsTopBar);
customElements.define("dweb-top-bar-button", BfcsTopBarButton);
