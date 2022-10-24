import { TextDecoder } from "node_util";
import { EventEmitter } from "node_event";

import { netCallNativeService } from "@bfsx/gateway";

import { NOTIFICATION_MESSAGE_PUSH, GET_NOTIFICATION } from "./constants.ts";
import { messageToQueue } from "./message_queue.ts";

import type * as types from "@bfsx/typings";
import type { IMessageSource } from "../typings/message.type.ts";

/**
 * 轮询拿取APP消息(Android端)
 * @param timeout - 延迟
 * @param ee      - 事件监听
 * @returns
 */
export async function asyncPollingCallDenoNotification(
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
        ? (JSON.parse(data.value) as IMessageSource)
        : undefined;

      if (messageInfo) {
        messageToQueue(messageInfo);
      }

      ee.emit(NOTIFICATION_MESSAGE_PUSH);
      break;
    } while (true);

    setTimeout(async () => {
      await polling();
    }, timeout);
  };

  await polling();
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

/**
 * 轮询拿取APP消息(iOS端)
 * @param timeout - 延迟
 * @param ee      - 事件监听
 * @returns
 */
export async function netCallNativeNotification(
  timeout = 3000,
  ee: EventEmitter
) {
  const polling = async () => {
    const messageString = await netCallNativeService(GET_NOTIFICATION);

    if (messageString) {
      const messages = JSON.parse(messageString);

      if (Array.isArray(messages) && messages.length > 0) {
        messages.forEach((message: IMessageSource) => {
          messageToQueue(message);
        });

        ee.emit(NOTIFICATION_MESSAGE_PUSH);
      }
    }

    setTimeout(async () => {
      await polling();
    }, timeout);
  };

  await polling();
}
