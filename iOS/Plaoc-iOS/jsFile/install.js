function BFSInstallApp (path) {
    window.webkit.messageHandlers.InstallBFS.postMessage({path:path});
}
