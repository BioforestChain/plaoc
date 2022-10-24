package org.bfchain.rust.plaoc

/** 系统调用函数*/
enum class ExportNative(val type: String) {
  OpenQrScanner("openQrScanner"),
  BarcodeScanner("barcodeScanner"),
  OpenDWebView("openDWebView"),
  InitMetaData("initMetaData"),
  DenoRuntime("denoRuntime"),
  ReadOnlyRuntime("readOnlyRuntime"),
  EvalJsRuntime("evalJsRuntime"),
  FileSystemLs("fileSystemLs"),
  FileSystemList("fileSystemList"),
  FileSystemMkdir("fileSystemMkdir"),
  FileSystemWrite("fileSystemWrite"),
  FileSystemRead("fileSystemRead"),
  FileSystemReadBuffer("fileSystemReadBuffer"),
  FileSystemRename("fileSystemRename"),
  FileSystemRm("fileSystemRm"),
  GetBfsAppId("getBfsAppId"),
  GetDeviceInfo("getDeviceInfo"),
  CreateNotificationMsg("createNotificationMsg"),
}

/** worker */
enum class WorkerNative{
  DenoRuntime,
  WorkerName,
  WorkerData
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
  GetKeyBoardSafeArea("getKeyBoardSafeArea"),
  GetKeyBoardHeight("getKeyBoardHeight"),
  GetKeyBoardOverlay("getKeyBoardOverlay"),
  SetKeyBoardOverlay("setKeyBoardOverlay"),
  ShowKeyBoard("showKeyBoard"),
  HideKeyBoard("hideKeyBoard"),
  // Top Bar
  TopBarNavigationBack("topBarNavigationBack"),
  GetTopBarShow("getTopBarShow"),
  SetTopBarShow("setTopBarShow"),
  GetTopBarOverlay("getTopBarOverlay"),
  SetTopBarOverlay("setTopBarOverlay"),
  GetTopBarAlpha("GetTopBarAlpha"),
  SetTopBarAlpha("SetTopBarAlpha"),
  GetTopBarTitle("getTopBarTitle"),
  SetTopBarTitle("setTopBarTitle"),
  HasTopBarTitle("hasTopBarTitle"),
  GetTopBarHeight("getTopBarHeight"),
  GetTopBarActions("getTopBarActions"),
  SetTopBarActions("setTopBarActions"),
  GetTopBarBackgroundColor("getTopBarBackgroundColor"),
  SetTopBarBackgroundColor("setTopBarBackgroundColor"),
  GetTopBarForegroundColor("getTopBarForegroundColor"),
  SetTopBarForegroundColor("setTopBarForegroundColor"),
  // bottomBar
  GetBottomBarEnabled("getBottomBarEnabled"),
  SetBottomBarEnabled("getBottomBarEnabled"),
  GetBottomBarAlpha("getBottomBarAlpha"),
  SetBottomBarAlpha("setBottomBarAlpha"),
  GetBottomBarHeight("getBottomBarHeight"),
  SetBottomBarHeight("setBottomBarHeight"),
  GetBottomBarActions("getBottomBarActions"),
  SetBottomBarActions("setBottomBarActions"),
  GetBottomBarBackgroundColor("getBottomBarBackgroundColor"),
  SetBottomBarBackgroundColor("setBottomBarBackgroundColor"),
  GetBottomBarForegroundColor("getBottomBarForegroundColor"),
  SetBottomBarForegroundColor("setBottomBarForegroundColor"),
  // Dialog
  OpenDialogAlert("openDialogAlert"),
  OpenDialogPrompt("openDialogPrompt"),
  OpenDialogConfirm("openDialogConfirm"),
  OpenDialogWarning("openDialogWarning")
}
