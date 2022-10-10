import metaData from "./bfsa-metadata.ts";
import { DWebView } from "@bfsx/core";
// export * from "./vfs/index.ts"

const webView = new DWebView(metaData);


// 多入口指定
webView.activity("index.html");

