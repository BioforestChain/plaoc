// 记得大写开头，跟kotlin enum  保持一直
export enum callDeno {
  openDWebView = "OpenDWebView",
  openQrScanner = "OpenQrScanner", // 二维码
  openBarcodeScanner = "BarcodeScanner", // 条形码
  initMetaData = "InitMetaData", //  初始化app数据
  denoRuntime = "DenoRuntime", // 初始化运行时
  evalJsRuntime = "EvalJsRuntime", // 传递给前端消息
  getDeviceInfo = "GetDeviceInfo", // 获取设备信息 
  sendNotification = "SendNotification", // 创建消息通知
}

// 回调到对应的组件
export enum callDVebView {
  BarcodeScanner = "dweb-scanner",
  OpenQrScanner = "dweb-scanner",
  OpenDWebView = "dweb-view",
}
// const callDeno
