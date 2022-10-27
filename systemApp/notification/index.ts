import { isAndroid } from "@bfsx/core";
import {
  asyncPollingCallDenoNotification,
  // netCallNativeNotification,
} from "./src/message_source.ts";
import "./bfsa-metadata.ts";

// 消息推送启动
export async function start() {
  console.log("开始运行消息推送无界面应用");
  // if (isAndroid) {
  await asyncPollingCallDenoNotification(1000);
  // } else {
  //   await netCallNativeNotification(1000);
  // }
}

try {
  start();
} catch (ex) {
  console.log(ex);
}
