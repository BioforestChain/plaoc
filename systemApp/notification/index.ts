import { currentPlatform } from "@bfsx/core";
import {
  asyncPollingCallDenoNotification,
  callNativeNotification,
} from "./src/message_source.ts";
import "./bfsa-metadata.ts";

// 消息推送启动
async function start() {
  console.log("开始运行消息推送无界面应用");

  switch (currentPlatform()) {
    case "Android":
      await asyncPollingCallDenoNotification(1000);
      break;
    case "iOS":
      await callNativeNotification();
      break;
    case "desktop-dev":
      break;
  }
}

try {
  start();
} catch (ex) {
  console.log(ex);
}
