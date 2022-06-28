/// <reference lib="dom" />

import "@plaoc/plugin-icon";
import "@plaoc/plugin-typings";

import { BfcsBottomBar } from "./bfcsBottomBar";
import { BfcsBottomBarButton } from "./bfcsBottomBarButton";

customElements.define("dweb-bottom-bar", BfcsBottomBar);
customElements.define("dweb-bottom-bar-button", BfcsBottomBarButton);
