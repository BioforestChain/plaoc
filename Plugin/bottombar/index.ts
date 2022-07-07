/// <reference lib="dom" />

import "@plaoc/plugin-icon";
import "@plaoc/plugin-typings";

import { BfcsBottomBar } from "./bfcsBottomBar";
import { BfcsBottomBarButton } from "./bfcsBottomBarButton";
import { BfcsBottomBarIcon } from "./bfcsBottomBarIcon";
import { BfcsBottomBarText } from "./bfcsBottomBarText";

customElements.define("dweb-bottom-bar", BfcsBottomBar);
customElements.define("dweb-bottom-bar-button", BfcsBottomBarButton);
customElements.define("dweb-bottom-bar-icon", BfcsBottomBarIcon);
customElements.define("dweb-bottom-bar-text", BfcsBottomBarText);
