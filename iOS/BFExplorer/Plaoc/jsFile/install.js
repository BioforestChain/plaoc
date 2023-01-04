function BFSInstallApp (path) {
    window.webkit.messageHandlers.InstallBFS.postMessage({path:path});
}

function getConnectChannel(data) {
    console.log("swift#getConnectChannel:",data)
    window.webkit.messageHandlers.getConnectChannel.postMessage({param:data});
}

function postConnectChannel(data,buffer) {
    window.webkit.messageHandlers.postConnectChannel.postMessage({param:data,buf: buffer});
}
