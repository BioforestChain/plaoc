// 记得大写开头，跟kotlin enum  保持一直
export enum callDeno {
  openDWebView = "OpenDWebView",
  openQrScanner = "OpenQrScanner",  // 二维码
  openBarcodeScanner = "BarcodeScanner", // 条形码
  initMetaData = "InitMetaData",
  denoRuntime = "DenoRuntime",
  evalJsRuntime = "EvalJsRuntime",
}

// 回调到对应的组件
export enum callDVebView {
  BarcodeScanner = "dweb-scanner",
  OpenQrScanner = "dweb-scanner",
  OpenDWebView = "dweb-view",
}
// const callDeno
