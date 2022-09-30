import { MetaData } from "@bfsx/metadata";
import metaData from "./bfsa-metadata.ts";
// const { metaData } = (await import("./bfsa-metadata", { assert: { type: "json" } }));

import { DWebView } from "@bfsx/core";

const webView = new DWebView(metaData);
// 多入口指定
webView.activity("index.html");

