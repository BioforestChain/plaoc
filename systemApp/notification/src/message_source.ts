import { TextDecoder } from "node_util";
import { EventEmitter } from "node_event";

import type { IMessageInfo } from "../typings/message.type.ts";
import {
  // NOTIFICATION_MESSAGE_QUEUE,
  NOTIFICATION_MESSAGE_PUSH,
} from "./constants.ts";
import { messageToQueue } from "./message_queue.ts";

import type * as types from "@bfsx/typings";
/**
 * 轮询拿取APP消息
 * @param timeout {number}
 * @returns
 */
export function asyncPollingCallDenoNotification(
  timeout = 3000,
  ee: EventEmitter
) {
  const polling = async () => {
    do {
      const data = await loopRustNotification().next();

      if (data.done) {
        continue;
      }

      const messageInfo = data.value
        ? (JSON.parse(data.value) as IMessageInfo)
        : undefined;

      if (messageInfo) {
        messageToQueue(messageInfo);
      }

      // TODO: 消息入队规则
      // 设备信息手机状态判断
      /**
       * if(手机处于静默状态) {
       *    continue;
       * }
       *
       * ee.emit(NOTIFICATION_MESSAGE_PUSH);
       * break;
       */
      // } while (NOTIFICATION_MESSAGE_QUEUE.length === 0);
      ee.emit(NOTIFICATION_MESSAGE_PUSH);
      break;
    } while (true);
  };

  setTimeout(async () => {
    await polling();
  }, timeout);
}

/**
 * 消息通知中心的消息在这里拿
 * @returns
 */
export function loopRustNotification() {
  return {
    async next() {
      let buffer: number[] = [];
      try {
        buffer = await Deno.core.opAsync("op_rust_to_js_app_notification");
        console.log("rust发送消息给deno_js:", buffer);
        const messageString = new TextDecoder().decode(new Uint8Array(buffer));
        console.log("rust发送消息给deno_js:", messageString);
        return {
          value: messageString,
          done: false,
        };
      } catch (_e) {
        return {
          value: "",
          done: true,
        };
      }
    },
    return() {
      /// 这里可以手动控制异步迭代器被 “中止” 释放
    },
    throw() {
      /// 这里可以手动控制异步迭代器被 “中止” 释放
    },
  };
}
