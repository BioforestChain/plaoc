import type * as types from "@bfsx/typings";
import { TextDecoder } from "node_util";

import type { IMessageInfo } from "../typings/message.type.ts";

/**
 * 轮询拿取APP消息
 * @param handleFn
 * @param data
 * @returns
 */
export function asyncCallDenoFunction(
  timeout: number = 3000
): Promise<IMessageInfo> {
  return new Promise<IMessageInfo>(async (resolve, reject) => {
    do {
      const data = await loopRustNotification().next();
      if (data.done) {
        continue;
      }

      const messageInfo = data.value ? JSON.parse(data.value) : undefined;
      resolve(messageInfo);
      break;
    } while (true);

    setTimeout(() => {
      reject("call function timeout"); // 超时
    }, timeout);
  });
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
