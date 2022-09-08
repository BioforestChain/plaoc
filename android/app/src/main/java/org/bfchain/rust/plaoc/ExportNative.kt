package org.bfchain.rust.plaoc


enum class ExportNative(val type: String) {
    OpenScanner("openScanner"),
    OpenDWebView("openDWebView"),
    InitMetaData("initMetaData"),
    DenoRuntime("denoRuntime"),
    EvalJsRuntime("evalJsRuntime"),
}

enum class ExportNativeUi(val type: String) {
  // Navigation
  SetNavigationBarVisible("setNavigationBarVisible"),
  SetNavigationBarColor("setNavigationBarColor"),
  GetNavigationBarVisible("getNavigationBarVisible"),
}
