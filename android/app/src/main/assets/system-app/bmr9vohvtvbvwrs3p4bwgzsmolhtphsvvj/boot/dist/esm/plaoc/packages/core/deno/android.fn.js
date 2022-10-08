// 记得大写开头，跟kotlin enum  保持一直
export var callDeno;
(function (callDeno) {
    callDeno["openDWebView"] = "OpenDWebView";
    callDeno["openQrScanner"] = "OpenQrScanner";
    callDeno["openBarcodeScanner"] = "BarcodeScanner";
    callDeno["initMetaData"] = "InitMetaData";
    callDeno["denoRuntime"] = "DenoRuntime";
    callDeno["evalJsRuntime"] = "EvalJsRuntime";
})(callDeno || (callDeno = {}));
// 回调到对应的组件
export var callDVebView;
(function (callDVebView) {
    callDVebView["BarcodeScanner"] = "dweb-scanner";
    callDVebView["OpenQrScanner"] = "dweb-scanner";
    callDVebView["OpenDWebView"] = "dweb-view";
})(callDVebView || (callDVebView = {}));
// const callDeno
