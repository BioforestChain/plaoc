/////////////////////////////
/// 这里负责把封装组件或者方法暴露出去（如果要实现按需导出，需要修改）
/////////////////////////////
export * from "./native/index";
export * from "./keyboard/index";
export * from "./bottombar/index";
export * from "./dialogs/index";
export * from "./icon/index";
export * from "./keyboard/index";
export * from "./statusbar/index";
export * from "./topbar/index";

export * from "./common/serverWorker";
import { registerServerWorker } from "./common/network";
registerServerWorker()

