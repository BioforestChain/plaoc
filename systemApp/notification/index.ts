import { EventEmitter } from "node_event";
import { TextEncoder } from "node_util";
import { setNotification } from "@bfsx/core";

import {
  NOTIFICATION_MESSAGE_QUEUE,
  NOTIFICATION_MESSAGE_PUSH,
} from "./src/constants.ts";
import { asyncPollingCallDenoNotification } from "./src/message_source.ts";
import "./bfsa-metadata.ts";

// 通过EventEmitter实现消息推送
export function messagePush() {
  const ee = new EventEmitter();
  asyncPollingCallDenoNotification(1000, ee);

  ee.on(NOTIFICATION_MESSAGE_PUSH, () => {
    for (const message of NOTIFICATION_MESSAGE_QUEUE) {
      const buffer = new TextEncoder().encode(JSON.stringify(message));

      setNotification(buffer);
    }
  });
}

messagePush();
