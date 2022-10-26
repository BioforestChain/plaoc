/////////////////////////////
/// 这里是所有DwebView-js的网关 负责发送消息到移动端，调用系统API。
/// 通信格式 :
/// https://channelId.bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/poll
/// https://channelId.bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj.dweb/setUi
/////////////////////////////

export * from './network.ts';
export * from "./service.ts";
export type { TNative } from './gateway.d.ts';
export * from "./serviceWorker.ts"
