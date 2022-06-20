//
//  CustomWebView.swift
//  WebViewController
//
//  Created by mac on 2022/3/15.
//

import UIKit
import WebKit

typealias UpdateTitleCallback = (String) -> Void

class CustomWebView: UIView {

    var callback: UpdateTitleCallback?
    var superVC: UIViewController?
    private var scripts: [WKUserScript]?
    private let imageUrl_key: String = "imageUrl"
    
    init(frame: CGRect, jsNames: [String]) {
        super.init(frame: frame)
        scripts = addUserScript(jsNames: jsNames)
        self.addSubview(webView)
        
        NotificationCenter.default.addObserver(self, selector: #selector(observerKeyboard(noti:)), name: UIResponder.keyboardWillShowNotification, object: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private lazy var webView: WKWebView = {
        
        let config = WKWebViewConfiguration()
        config.userContentController = WKUserContentController()
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "hiddenBottomView")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateBottomViewAlpha")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateBottomViewBackgroundColor")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "hiddenBottomViewButton")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "hiddenNaviBarButton")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateStatusAlpha")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateStatusBackgroundColor")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateStatusStyle")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateStatusHidden")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "hiddenNaviBar")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateNaviBarAlpha")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateNaviBarBackgroundColor")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateNaviBarTintColor")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "backAction")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "jumpWeb")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "updateTitle")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "startLoad")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "LoadingComplete")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "onPush")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "onFork")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "onPop")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "onDestroy")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "onCheckout")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "savePhoto")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "startCamera")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "photoFromPhotoLibrary")
        config.userContentController.add(LeadScriptHandle(messageHandle: self), name: "startShare")
        config.userContentController.addScriptMessageHandler(self, contentWorld: .page, name: "test")
        config.userContentController.addScriptMessageHandler(self, contentWorld: .page, name: "calendar")
        config.userContentController.addScriptMessageHandler(self, contentWorld: .page, name: "naviHeight")
        config.userContentController.addScriptMessageHandler(self, contentWorld: .page, name: "bottomHeight")
        if self.scripts != nil {
            for script in self.scripts! {
                config.userContentController.addUserScript(script)
            }
        }
        let prefreen = WKPreferences()
        prefreen.javaScriptCanOpenWindowsAutomatically = true
        config.preferences = prefreen
        config.setValue(true, forKey: "allowUniversalAccessFromFileURLs")
        let webView = WKWebView(frame: self.bounds, configuration: config)
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.allowsBackForwardNavigationGestures = true
        if #available(iOS 11.0, *) {
            webView.scrollView.contentInsetAdjustmentBehavior = .never
        } else {
            
        }
        return webView
    }()
    
}

extension CustomWebView {
    //注入脚本
    private func addUserScript(jsNames: [String]) -> [WKUserScript]? {
        guard jsNames.count > 0 else { return nil }
        var scripts: [WKUserScript] = []
        for jsName in jsNames {
            if let path = Bundle.main.path(forResource: jsName, ofType: "js") {
                let url = URL(fileURLWithPath: path)
                let data = try? Data(contentsOf: url)
                if data != nil {
                    if let jsString = String(data: data!, encoding: .utf8) {
                        let script = WKUserScript(source: jsString, injectionTime: .atDocumentStart, forMainFrameOnly: true)
                        scripts.append(script)
                    }
                }
            }
        }
        return scripts.count > 0 ? scripts : nil
    }
    
    func openWebView(html: String) {
        if let url = URL(string: html) {
            let request = URLRequest(url: url)
            self.webView.load(request)
        }
    }
    
    func openLocalWebView(name: String) {

        var path = name
        path = "file://".appending(path)
        if let url = URL(string: path) {
            self.webView.load(URLRequest(url: url))
        }
    }
    
    func handleJavascriptString(inputJS: String) {
        webView.evaluateJavaScript(inputJS) { (response, error) in
            print( response , error)
        }
    }

    func closeWebViewBackForwardNavigationGestures(isClose: Bool) {
        webView.allowsBackForwardNavigationGestures = !isClose
    }
    
    func canGoback() -> Bool {
        return webView.canGoBack
    }
    
    func goBack() {
        if webView.canGoBack {
            webView.goBack()
        }
    }
    
    @objc private func observerKeyboard(noti: Notification) {
        
        guard let keyboardBound = noti.userInfo?["UIKeyboardFrameEndUserInfoKey"] as? CGRect else { return }
        let keyboardString = "getKeyboardFrame('\(keyboardBound)')"
        handleJavascriptString(inputJS: keyboardString)
        
        /**
         ([AnyHashable("UIKeyboardAnimationCurveUserInfoKey"): 7,
         AnyHashable("UIKeyboardBoundsUserInfoKey"): NSRect: {{0, 0}, {375, 380}},
         AnyHashable("UIKeyboardCenterBeginUserInfoKey"): NSPoint: {187.5, 1002},
         AnyHashable("UIKeyboardIsLocalUserInfoKey"): 1,
         AnyHashable("UIKeyboardFrameEndUserInfoKey"): NSRect: {{0, 432}, {375, 380}},
         AnyHashable("UIKeyboardFrameBeginUserInfoKey"): NSRect: {{0, 812}, {375, 380}},
         AnyHashable("UIKeyboardAnimationDurationUserInfoKey"): 0.25,
         AnyHashable("UIKeyboardCenterEndUserInfoKey"): NSPoint: {187.5, 622}])
         */
    }
}

extension CustomWebView:  WKScriptMessageHandler {
    //通过js调取原生操作
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.name == "startLoad" {
            //点击网页按钮 开始加载
        } else if message.name == "jumpWeb" {
            guard let bodyString = message.body as? String else { return }
            let controller = currentViewController()
            let second = WebViewViewController()
            second.urlString = bodyString
            controller.navigationController?.pushViewController(second, animated: true)
        }else if message.name == "updateTitle" {
            guard let titleString = message.body as? String else { return }
            callback?(titleString)
        } else if message.name == "backAction" {
            let controller = currentViewController()
            controller.navigationController?.popViewController(animated: true)
        } else if message.name == "hiddenNaviBar" {
            let controller = currentViewController() as? WebViewViewController
            controller?.hiddenNavigationBar()
        } else if message.name == "updateNaviBarAlpha" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateNavigationBarAlpha()
        } else if message.name == "updateNaviBarBackgroundColor" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateNavigationBarBackgroundColor()
        } else if message.name == "updateNaviBarTintColor" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateNavigationBarTintColor()
        } else if message.name == "updateStatusBackgroundColor" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateStatusBackgroundColor()
        } else if message.name == "updateStatusStyle" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateStatusStyle()
        } else if message.name == "updateStatusHidden" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateStatusHidden()
        } else if message.name == "updateStatusAlpha" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateStatusBarAlpha()
        } else if message.name == "hiddenNaviBarButton" {
            guard let bodyString = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            controller?.hiddenNaviButton(hiddenString: bodyString)
        }  else if message.name == "hiddenBottomView" {
            let controller = currentViewController() as? WebViewViewController
            controller?.hiddenBottomView()
        } else if message.name == "updateBottomViewAlpha" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateBottomViewAlpha()
        } else if message.name == "updateBottomViewBackgroundColor" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateBottomViewBackgroundColor()
        } else if message.name == "hiddenBottomViewButton" {
            guard let bodyString = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            controller?.hiddenBottomViewButton(hiddenString: bodyString)
        }
    }
    
    func updateFrame(frame: CGRect) {
        webView.frame = CGRect(origin: .zero, size: frame.size)
    }
}


extension CustomWebView: WKScriptMessageHandlerWithReply {
    
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage, replyHandler: @escaping (Any?, String?) -> Void) {
        
        if message.name == "calendar" {
//            showCalendarView(frame: CGRect(x: 0, y: 200, width: UIScreen.main.bounds.width, height: 300))
            
            let documentPicker = UIDocumentPickerViewController.init(documentTypes: ["public.data"], in: .import)
            documentPicker.delegate = self
            documentPicker.modalPresentationStyle = .popover
            let controller = currentViewController()
            controller.present(documentPicker, animated: true)
        } else if message.name == "naviHeight" {
            let naviHeight = UIDevice.current.statusBarHeight() + 44
            replyHandler(naviHeight,nil)
        } else if message.name == "bottomHeight" {
            let naviHeight = 49 + UIDevice.current.tabbarSpaceHeight()
            replyHandler(naviHeight,nil)
        }
    }
}

extension CustomWebView: UIDocumentPickerDelegate {
    
    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        controller.dismiss(animated: true)
        print(urls)
    }
    
    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        controller.dismiss(animated: true)
    }
}

extension CustomWebView: WKNavigationDelegate {
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
       
        decisionHandler(.allow)
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        
        decisionHandler(.allow)
    }
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
      
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("didFinish")
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        print("didFailProvisionalNavigation")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        print("didFail")
    }
    
    func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
        
    }
    
    func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
        print("webViewWebContentProcessDidTerminate")
    }
    
}


extension CustomWebView: WKUIDelegate {
    
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let controller = self.currentViewController()
        let alert = UIAlertController(title: "TIPS", message: message, preferredStyle: .alert)
        let sureAction = UIAlertAction(title: "OK", style: .default) { action in
            completionHandler()
        }
        alert.addAction(sureAction)
        controller.present(alert, animated: true)
        
    }
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void){
        let controller = self.currentViewController()
        let alert = UIAlertController(title: "TIPS", message: message, preferredStyle: .alert)
        let sureAction = UIAlertAction(title: "OK", style: .default) { action in
            completionHandler(true)
        }
        alert.addAction(sureAction)
        controller.present(alert, animated: true)
        
    }
    func webView(_ webView: WKWebView, runJavaScriptTextInputPanelWithPrompt prompt: String, defaultText: String?, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (String?) -> Void){
        let controller = self.currentViewController()
        let alert = UIAlertController(title: "TIPS", message: defaultText, preferredStyle: .alert)
        let sureAction = UIAlertAction(title: "OK", style: .default) { action in
            completionHandler(alert.textFields?.first?.text ?? "")
        }
        alert.addAction(sureAction)
        alert.addTextField { textField in
            textField.text = prompt
            textField.placeholder = defaultText
        }
        controller.present(alert, animated: true)
    }
    
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        let ismain = navigationAction.targetFrame?.isMainFrame ?? true
        print(ismain)
        if ismain {
            let wk = WKWebView(frame: webView.frame, configuration: configuration)
            wk.uiDelegate = self
            wk.navigationDelegate = self
            wk.load(navigationAction.request)
            
            let vc = UIViewController()
            vc.modalPresentationStyle = .fullScreen
            vc.view = wk
            
            let controller = currentViewController()
            controller.navigationController?.pushViewController(vc, animated: true)
            return wk
            
        }
        return nil
    }
}
