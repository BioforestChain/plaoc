import metaData from "./bfsa-metadata.ts";
import { DWebView, sendNotification } from "@bfsx/core";
import { EChannelMode } from "@bfsx/typings";
// export * from "./vfs/index.ts"
    // console.log("window.Deno.core.ops--->",window.Deno.core.ops)

const webView = new DWebView(metaData);

(async () => {
  await webView.openRequest("/api/*", EChannelMode.pattern)
})()

// 多入口指定
webView.activity("index.html");


try {
  sendNotification({ title: "消息头", body: "今晚打老虎", priority: 1 });
} catch (error) {
  console.log(error)
}

