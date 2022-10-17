/// <reference lib="dom" />

import { BfcsBottomBar } from "./bfcsBottomBar.ts";
import { BfcsBottomBarButton } from "./bfcsBottomBarButton.ts";
import { BfcsBottomBarIcon } from "./bfcsBottomBarIcon.ts";
import { BfcsBottomBarText } from "./bfcsBottomBarText.ts";

export {
  BfcsBottomBar
}

customElements.define("dweb-bottom-bar", BfcsBottomBar);
customElements.define("dweb-bottom-bar-button", BfcsBottomBarButton);
customElements.define("dweb-bottom-bar-icon", BfcsBottomBarIcon);
customElements.define("dweb-bottom-bar-text", BfcsBottomBarText);
