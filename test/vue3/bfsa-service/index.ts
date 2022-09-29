import { MetaData } from "@bfsx/metadata";
import { metaData } from "./bfsa-metadata.ts";

import { DWebView } from "@bfsx/core";

const webView = new DWebView(new MetaData(metaData));
// 多入口指定
webView.activity("index.html");

