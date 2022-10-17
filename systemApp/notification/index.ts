import { EventEmitter } from "node_event";

import "./bfsa-metadata.ts";
import {
  NOTIFICATION_MESSAGE_QUEUE,
  NOTIFICATION_MESSAGE_PUSH,
} from "./src/constants.ts";
import { asyncPollingCallDenoNotification } from "./src/message_source.ts";

export function messagePush() {
  const ee = new EventEmitter();
  asyncPollingCallDenoNotification(1000, ee);

  ee.on(NOTIFICATION_MESSAGE_PUSH, () => {
    // TODO: 消息推送
    // NOTIFICATION_MESSAGE_QUEUE
    // 通过notification system api
  });
}

messagePush();
