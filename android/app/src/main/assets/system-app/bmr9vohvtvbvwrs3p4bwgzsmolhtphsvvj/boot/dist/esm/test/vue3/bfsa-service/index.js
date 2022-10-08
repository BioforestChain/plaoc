import metaData from "./bfsa-metadata.js";
// const { metaData } = (await import("./bfsa-metadata", { assert: { type: "json" } }));
import { DWebView } from "../../../plaoc/packages/core/index.js";
const webView = new DWebView(metaData);
// 多入口指定
webView.activity("index.html");
