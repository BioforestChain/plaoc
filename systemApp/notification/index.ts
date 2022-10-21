import { EventEmitter } from "node_event";
import { isAndroid } from "@bfsx/core";

import { NOTIFICATION_MESSAGE_PUSH } from "./src/constants.ts";
import {
  asyncPollingCallDenoNotification,
  netCallNativeNotification,
} from "./src/message_source.ts";
import { messagePush } from "./src/message_push.ts";
import "./bfsa-metadata.ts";

// 通过EventEmitter实现消息推送
export async function start() {
  const ee = new EventEmitter();

  if (isAndroid) {
    await asyncPollingCallDenoNotification(1000, ee);
  } else {
    await netCallNativeNotification(1000, ee);
  }

  let locked = false;
  ee.on(NOTIFICATION_MESSAGE_PUSH, async () => {
    if (!locked) {
      locked = true;
      await messagePush();
      locked = false;
    }
  });
}

start();
