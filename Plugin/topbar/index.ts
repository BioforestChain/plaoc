/// <reference lib="dom" />

import "@plaoc/plugin-icon";
import "@plaoc/plugin-typings";

import { BfcsTopBar } from "./bfcsTopBar";
import { BfcsTopBarButton } from "./bfcsTopBarButton";
customElements.define("dweb-top-bar", BfcsTopBar);
customElements.define("dweb-top-bar-button", BfcsTopBarButton);
