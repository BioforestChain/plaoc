package org.bfchain.rust.plaoc


enum class ExportNative(val type: String) {
    OpenScanner("openScanner"),
    OpenDWebView("openDWebView"),
    InitMetaData("initMetaData"),
    DenoRuntime("denoRuntime"),
    EvalJsRuntime("evalJsRuntime"),
}

