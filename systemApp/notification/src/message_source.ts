import { GET_NOTIFICATION, NOTIFICATION_MESSAGE_PUSH } from "./constants.ts";
import { messageToQueue, messageToQueueIOS } from "./message_queue.ts";
import { messagePush } from "./message_push.ts";

import type { IMessageSource } from "../typings/message.type.ts";
import type * as types from "@bfsx/typings";

/**
 * 轮询拿取APP消息(Android端)
 * @param timeout - 延迟
 * @returns
 */
let isLocked = false;
export async function asyncPollingCallDenoNotification(timeout = 3000) {
  const listener = async () => {
    isLocked = true;
    await messagePush();
    isLocked = false;
  };
  const polling = async () => {
    do {
      const data = await loopRustNotification().next();

      if (data.done) {
        break;
      }

      const messageInfo = data.value
        ? (JSON.parse(data.value) as IMessageSource)
        : undefined;

      if (messageInfo) {
        messageToQueue(messageInfo);
      }

      addEventListener(NOTIFICATION_MESSAGE_PUSH, listener);
    } while (true);

    if (!isLocked) {
      dispatchEvent(new Event(NOTIFICATION_MESSAGE_PUSH));
    } else {
      removeEventListener(NOTIFICATION_MESSAGE_PUSH, listener);
    }

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

export async function callNativeNotification() {
  // dnt-shim-ignore
  const jscore = (globalThis as any)
    .PlaocJavascriptBridge as types.PlaocJavascriptBridge;

  const result = jscore.callJavaScriptWithFunctionNameParam(
    GET_NOTIFICATION,
    ""
  );

  const messageString = String.fromCharCode(...result);

  if (messageString) {
    const messages = JSON.parse(messageString);

    if (Array.isArray(messages) && messages.length > 0) {
      messageToQueueIOS(messages);
    }
  }

  if (!isLocked) {
    isLocked = true;
    await messagePush();
    isLocked = false;
  }

  return;
}
