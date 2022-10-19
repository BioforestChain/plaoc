import { network } from "@bfsx/core";
import {
  NOTIFICATION_MESSAGE_QUEUE,
  CREATE_NOTIFICATION_MSG,
} from "./constants.ts";

import {
  type IMessageInfoPush,
  MessagePriority,
} from "../typings/message.type.ts";

/** 消息推送 */
export async function messagePush() {
  // TODO(@kingsowrd09): 添加判断手机模式
  while (NOTIFICATION_MESSAGE_QUEUE.length > 0) {
    const item = NOTIFICATION_MESSAGE_QUEUE.shift()!;
    const message: IMessageInfoPush = {
      ...item,
      priority:
        item.priority < MessagePriority.IMPORTANT_MESSAGE
          ? MessagePriority.DELAY_MESSAGE
          : MessagePriority.IMPORTANT_MESSAGE,
    };

    // 推送接口
    await network.asyncCallDenoFunction(CREATE_NOTIFICATION_MSG, message);
  }

  return;
}
