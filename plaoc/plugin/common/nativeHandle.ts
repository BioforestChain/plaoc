export enum NativeHandle {
  OpenScanner = "OpenScanner",
}

export enum NativeUI {
  // Navigation
  SetNavigationBarVisible = "SetNavigationBarVisible",
  GetNavigationBarVisible = "GetNavigationBarVisible",
  SetNavigationBarColor = "SetNavigationBarColor",
  SetNavigationBarOverlay = "SetNavigationBarOverlay",
  GetNavigationBarOverlay = "GetNavigationBarOverlay",
  // Status Bar
  SetStatusBarColor = "SetStatusBarColor",
  GetStatusBarColor = "GetStatusBarColor",
  GetStatusBarIsDark = "GetStatusBarIsDark",
  GetStatusBarVisible = "GetStatusBarVisible",
  GetStatusBarOverlay = "GetStatusBarOverlay",
  SetStatusBarOverlay = "SetStatusBarOverlay",
  SetStatusBarVisible = "SetStatusBarVisible",

  // Top Bar
  TopBarNavigationBack = "TopBarNavigationBack",
  GetTopBarEnabled = "GetTopBarEnabled",
  SetTopBarEnabled = "SetTopBarEnabled",
  GetTopBarOverlay = "GetTopBarOverlay",
  SetTopBarOverlay = "SetTopBarOverlay",
  GetTopBarTitle = "GetTopBarTitle",
  SetTopBarTitle = "SetTopBarTitle",
  HasTopBarTitle = "HasTopBarTitle",
  GetTopBarHeight = "GetTopBarHeight",
  GetTopBarActions = "GetTopBarActions",
  SetTopBarActions = "SetTopBarActions",
  GetTopBarBackgroundColor = "GetTopBarBackgroundColor",
  SetTopBarBackgroundColor = "SetTopBarBackgroundColor",
  GetTopBarForegroundColor = "GetTopBarForegroundColor",
  SetTopBarForegroundColor = "SetTopBarForegroundColor",

  // keyboard
  GetSafeArea = "GetSafeArea",
  GetHeight = "GetHeight",
  GetOverlay = "GetOverlay",
  SetOverlay = "SetOverlay",
  Show = "Show",
  Hide = "Hide",
}
