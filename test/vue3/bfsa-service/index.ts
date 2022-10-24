import metaData from "./bfsa-metadata.ts";
import { DWebView, sendNotification } from "@bfsx/core";
export * from "./vfs/index.ts"

const webView = new DWebView(metaData);


// 多入口指定
webView.activity("index.html");


try {
  sendNotification({ title: "消息头", body: "今晚打老虎", priority: 1 });
} catch (error) {
  console.log(error)
}

