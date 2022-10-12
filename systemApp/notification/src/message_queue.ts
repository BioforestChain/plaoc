/**
 * 消息队列
 */

import {
  type IMessageInfo,
  type IMessageInfoExtension,
  MessagePriority,
  MessageStatus,
} from "../typings/message.type.ts";

import SnowFlake from "../utils/snowFlake.ts";
import { NOTIFICATION_MESSAGE_QUEUE, CODE_MAP } from "./constants.ts";

/**
 * 消息入队列
 * @param messageInfo 消息体
 */
export function messageToQueue(messageInfo: IMessageInfo) {
  const msg_priority = messagePriorityInit(messageInfo.priority);
  const msg_id = genMessageIds(messageInfo.app_id);

  const messageInfoItem: IMessageInfoExtension = {
    ...messageInfo,
    msg_id,
    msg_priority,
    entry_queue_time: Date.now(),
    msg_status: MessageStatus.UNPROCESS,
  };

  NOTIFICATION_MESSAGE_QUEUE.push(messageInfoItem);
  messagePriorityAscension();

  // 根据优先级排序
  NOTIFICATION_MESSAGE_QUEUE.sort((a, b) => b.msg_priority - a.msg_priority);

  return;
}

/**
 * 消息优先级提升
 */
function messagePriorityAscension() {
  for (const [index, item] of NOTIFICATION_MESSAGE_QUEUE.entries()) {
    // 如果状态为已处理，移除出队列
    if (item.msg_status === MessageStatus.PROCESSED) {
      NOTIFICATION_MESSAGE_QUEUE.splice(index, 1);
    }

    /**
     * 消息优先级提升策略
     * 当前时间与入队列时间每相差1分钟，按优先级提升不同的权重，越不重要的消息提升越多
     * 重要消息提升： 1
     * 即时消息提升： 5
     * 延迟消息提升： 10
     */
    const n = ((Date.now() - item.entry_queue_time) % 1000) * 60;
    if (n > 0) {
      switch (item.priority) {
        case MessagePriority.IMPORTANT_MESSAGE:
          item.msg_priority <= 99
            ? (item.msg_priority += 1)
            : (item.msg_priority = 100);
          break;
        case MessagePriority.INSTANCE_MESSAGE:
          item.msg_priority <= 95
            ? (item.msg_priority += 5)
            : (item.msg_priority = 100);
          break;
        case MessagePriority.DELAY_MESSAGE:
          item.msg_priority <= 90
            ? (item.msg_priority += 10)
            : (item.msg_priority = 100);
          break;
      }
    }
  }
}

/**
 * 消息优先级初始化
 * @param priority 优先级
 * @returns
 */
function messagePriorityInit(priority: MessagePriority): number {
  let msg_priority = 0;

  switch (priority) {
    case MessagePriority.IMPORTANT_MESSAGE:
      msg_priority = 90;
      break;
    case MessagePriority.INSTANCE_MESSAGE:
      msg_priority = 50;
      break;
    case MessagePriority.DELAY_MESSAGE:
      msg_priority = 0;
      break;
    default:
      break;
  }

  return msg_priority;
}

/**
 * 生成msg_id
 * @param bfsAppId 应用id
 * @returns
 */
function genMessageIds(bfsAppId: string): bigint {
  let tempWorkerId: string = "";
  let tempDataCenterId: string = "";
  for (const [index, item] of bfsAppId.split("").entries()) {
    // 取bfsAppId前四位为workerId
    if (index < 4) {
      tempWorkerId += CODE_MAP.get(item);
    } else {
      // 后四位为dataCenterId
      tempDataCenterId += CODE_MAP.get(item);
    }
  }

  const workerId: bigint = BigInt(tempWorkerId);
  const dataCenterId: bigint = BigInt(tempDataCenterId);

  // 雪花算法生成msg_id
  const snowFlake = new SnowFlake(workerId, dataCenterId);
  return snowFlake.nextId();
}
