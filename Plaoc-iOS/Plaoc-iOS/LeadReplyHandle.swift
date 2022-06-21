//
//  LeadReplyHandle.swift
//  DWebBrowser
//
//  Created by mac on 2022/5/11.
//

import UIKit
import WebKit

class LeadReplyHandle: NSObject, WKScriptMessageHandlerWithReply {
  
    var scriptDelegate: WKScriptMessageHandlerWithReply?

    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage, replyHandler: @escaping (Any?, String?) -> Void) {
        self.scriptDelegate?.userContentController(userContentController, didReceive: message, replyHandler: replyHandler)
    }

    init(messageHandle: WKScriptMessageHandlerWithReply) {
        super.init()
        self.scriptDelegate = messageHandle
    }
}
