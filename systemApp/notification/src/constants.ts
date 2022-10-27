/**
 * 常量
 */

import type { IMessageInfo } from "../typings/message.type.ts";

// 通知消息队列
export const NOTIFICATION_MESSAGE_QUEUE: IMessageInfo[] = [];

// 消息通知
export const NOTIFICATION_MESSAGE_PUSH = "notification_message_push";

// native
export const CREATE_NOTIFICATION_MSG = "CreateNotificationMsg";

// ios getNotification
export const GET_NOTIFICATION = "GetNotification";

// 用于生成workerId, dataCenterId
export const CODE_MAP = new Map<string, string>([
  ["0", "0"],
  ["1", "1"],
  ["2", "2"],
  ["3", "3"],
  ["4", "4"],
  ["5", "5"],
  ["6", "6"],
  ["7", "7"],
  ["8", "8"],
  ["9", "9"],
  ["A", "10"],
  ["B", "11"],
  ["C", "12"],
  ["D", "13"],
  ["E", "14"],
  ["F", "15"],
  ["G", "16"],
  ["H", "17"],
  ["J", "18"],
  ["K", "19"],
  ["L", "20"],
  ["M", "21"],
  ["N", "22"],
  ["P", "23"],
  ["Q", "24"],
  ["R", "25"],
  ["T", "26"],
  ["U", "27"],
  ["W", "28"],
  ["X", "29"],
  ["Y", "30"],
]);
