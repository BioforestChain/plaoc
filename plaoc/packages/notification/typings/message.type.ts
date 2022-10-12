/** 消息优先级 */
export enum MessagePriority {
  // 重要消息
  IMPORTANT_MESSAGE = 1,
  // 即时消息
  INSTANCE_MESSAGE,
  // 延迟消息
  DELAY_MESSAGE,
}

/** 消息来源 */
export enum MessageSource {
  // 应用内消息
  APP = "app_message",
  // 推送平台
  PUSH = "push_message",
  // // 短信消息
  // SMS = "sms_message",
}

/** 消息体 */
export interface IMessageInfo {
  // 应用id
  app_id: string;
  // 消息标题
  title?: string;
  // 消息详情
  msg_content: string;
  // 消息来源
  msg_src: MessageSource;
  // 消息优先级
  priority: MessagePriority;
  // 消息推送时间 YYYYMMDD
  time: string;
}

/** 消息处理状态 */
export enum MessageStatus {
  // 未处理
  UNPROCESS,
  // 已处理
  PROCESSED,
}

/** 手机模式 */
export enum PhoneStatus {
  // 勿扰模式、专注模式、静音模式
  SILENT_MODE,
  // 正常模式
  NORMAL_MODE,
}

/** 扩展消息体，用于队列优先级处理 */
export interface IMessageInfoExtension extends IMessageInfo {
  // 消息id，类似：18100287420699802
  msg_id: bigint;
  // 入队时间：timestamp，用于长时间未能处于优先级较高时，提升优先级，避免饿死
  entry_queue_time: number;
  // 当前消息处理状态
  msg_status: MessageStatus;
  // 消息处理优先数值 0-100
  msg_priority: number;
  // 手机系统状态
  phone_status: PhoneStatus;
}
