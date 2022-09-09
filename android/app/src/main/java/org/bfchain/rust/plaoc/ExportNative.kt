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
  SetNavigationBarOverlay("setNavigationBarOverlay"),
  GetNavigationBarOverlay("getNavigationBarOverlay"),
  // Status Bar
  GetStatusBarColor("getStatusBarColor"),
  SetStatusBarColor("setStatusBarColor"),
  GetStatusBarIsDark("getStatusBarIsDark"),
  GetStatusBarVisible("getStatusBarVisible"),
  SetStatusBarVisible("setStatusBarVisible"),
  GetStatusBarOverlay("getStatusBarOverlay"),
  SetStatusBarOverlay("setStatusBarOverlay"),
  // keyboard
  GetSafeArea("getSafeArea"),
  GetHeight("getHeight"),
  GetOverlay("getOverlay"),
  SetOverlay("setOverlay"),
  Show("show"),
  Hide("hide"),

}
