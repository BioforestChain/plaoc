import { MetaData } from "@bfsx/metadata";
import { metaData } from "./BFS-Metadata";

import { DWebView } from "@bfsx/core";

const webView = new DWebView(new MetaData(metaData));
webView.activity();
