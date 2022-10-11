/**
 * 消息队列
 */

import { type IMessageInfo, MessagePriority } from "../typings/message.type.ts";

import { NOTIFICATION_MESSAGE_QUEUE } from "./constants.ts";

/**
 * 消息优先级分类入队列
 * @param messageInfo 消息体
 */
export function messageClassificationToQueue(messageInfo: IMessageInfo) {
  for await (const item of NOTIFICATION_MESSAGE_QUEUE) {
  }
}

async function messagePriorityAscension() {}
