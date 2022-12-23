import metaData from "./bfsa-metadata.ts";
import { DWebView, sendNotification } from "@bfsx/core";
import { EChannelMode } from "@bfsx/typings";
// export * from "./vfs/index.ts";

const webView = new DWebView(metaData);

// 打开channel模式
(async () => {
  await webView.openRequest("/api/*", EChannelMode.pattern);
})();

// 多入口指定
webView.activity("https://objectjson.waterbang.top/");

try {
  sendNotification({ title: "消息头", body: "今晚打老虎", priority: 1 });
} catch (error) {
  console.log(error);
}
