/////////////////////////////
/// 这里负责把封装组件或者方法暴露出去（如果要实现按需导出，需要修改）
/////////////////////////////
export * from "./native/index.ts";
// export * as native from "./native/index";
// export * from "./native/dweb-plugin";
export * from "./keyboard/index.ts";
export * from "./bottombar/index.ts";
export * from "./dialogs/index.ts";
export * from "./icon/index.ts";
export * from "./statusbar/index.ts";
export * from "./topbar/index.ts";
export * from "./types/index.ts";

import { registerServiceWorker } from "@bfsx/gateway";
registerServiceWorker();
