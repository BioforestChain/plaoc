import { network, getDeviceInfo, EDeviceModule } from "@bfsx/core";
import {
  NOTIFICATION_MESSAGE_QUEUE,
  CREATE_NOTIFICATION_MSG,
} from "./constants.ts";

import {
  type IMessageInfo,
  MessagePriority,
  MessageStatus,
} from "../typings/message.type.ts";

/** 消息推送 */
export async function messagePush() {
  /**
   * 设备信息手机状态判断
   * 如果消息大于等于20条，开始推送
   */
  const info = await getDeviceInfo();
  if (
    info.module === EDeviceModule.doNotDisturb &&
    NOTIFICATION_MESSAGE_QUEUE.length < 20
  ) {
    return;
  }

  /**
   * @TODO 是否需要添加重试次数
   */
  for (const item of NOTIFICATION_MESSAGE_QUEUE) {
    if (item.msg_status === MessageStatus.UNPROCESS) {
      const message: IMessageInfo = {
        ...item,
        priority:
          item.priority < MessagePriority.IMPORTANT_MESSAGE
            ? MessagePriority.DELAY_MESSAGE
            : MessagePriority.IMPORTANT_MESSAGE,
      };

      // 推送接口
      await network.asyncCallDenoFunction(CREATE_NOTIFICATION_MSG, message);

      // 更新消息为已完成
      item.msg_status = MessageStatus.PROCESSED;
    }
  }

  return;
}
