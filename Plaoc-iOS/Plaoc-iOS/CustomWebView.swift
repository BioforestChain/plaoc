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
        addScriptMessageHandler(config: config)
        addScriptMessageHandlerWithReply(config: config)
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
    
    private func addScriptMessageHandler(config: WKWebViewConfiguration) {
        let array = ["hiddenBottomView","updateBottomViewAlpha","updateBottomViewBackgroundColor","hiddenBottomViewButton","hiddenNaviBarButton","updateStatusAlpha","updateStatusBackgroundColor","updateStatusStyle","updateStatusHidden","hiddenNaviBar","updateNaviBarAlpha","updateNaviBarBackgroundColor","updateNaviBarTintColor","back","jumpWeb","updateTitle","startLoad","LoadingComplete","savePhoto","startCamera","photoFromPhotoLibrary","startShare"]
        for name in array {
            config.userContentController.add(LeadScriptHandle(messageHandle: self), name: name)
        }
    }
    
    private func addScriptMessageHandlerWithReply(config: WKWebViewConfiguration) {
        let array = ["calendar","naviHeight","bottomHeight","getNaviEnabled","hasNaviTitle","getNaviOverlay","getNaviBackgroundColor","getNaviForegroundColor"]
        for name in array {
            config.userContentController.addScriptMessageHandler(self, contentWorld: .page, name: name)
        }
    }
    
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
        } else if message.name == "back" {
            let controller = currentViewController()
            controller.navigationController?.popViewController(animated: true)
        } else if message.name == "hiddenNaviBar" {
            guard let hidden = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            let isHidden = hidden == "1" ? true : false
            controller?.hiddenNavigationBar(isHidden: isHidden)
        } else if message.name == "updateNaviBarAlpha" {
            guard let alpha = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            let isAlpha = alpha == "1" ? true : false
            controller?.updateNavigationBarAlpha(isAlpha: isAlpha)
        } else if message.name == "updateNaviBarBackgroundColor" {
            guard let colorString = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            controller?.updateNavigationBarBackgroundColor(colorString: colorString)
        } else if message.name == "updateNaviBarTintColor" {
            guard let colorString = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            controller?.updateNavigationBarTintColor(colorString: colorString)
        } else if message.name == "updateStatusBackgroundColor" {
            guard let colorString = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            controller?.updateStatusBackgroundColor(colorString: colorString)
        } else if message.name == "updateStatusStyle" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateStatusStyle()
        } else if message.name == "updateStatusHidden" {
            guard let hidden = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            let isHidden = hidden == "1" ? true : false
            controller?.updateStatusHidden(isHidden: isHidden)
        } else if message.name == "updateStatusAlpha" {
            let controller = currentViewController() as? WebViewViewController
            controller?.updateStatusBarAlpha()
        } else if message.name == "hiddenNaviBarButton" {
            guard let bodyString = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            controller?.hiddenNaviButton(hiddenString: bodyString)
        }  else if message.name == "hiddenBottomView" {
            guard let hidden = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            let isHidden = hidden == "1" ? true : false
            controller?.hiddenBottomView(isHidden: isHidden)
        } else if message.name == "updateBottomViewAlpha" {
            guard let alpha = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            let isAlpha = alpha == "1" ? true : false
            controller?.updateBottomViewAlpha(isAlpha: isAlpha)
        } else if message.name == "updateBottomViewBackgroundColor" {
            guard let colorString = message.body as? String else { return }
            let controller = currentViewController() as? WebViewViewController
            controller?.updateBottomViewBackgroundColor(colorString: colorString)
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
        } else if message.name == "getNaviEnabled" {
            let controller = currentViewController() as? WebViewViewController
            let isHidden = controller?.navigationController?.isNavigationBarHidden
            replyHandler(isHidden,nil)
        } else if message.name == "getNaviTitle" {
            let controller = currentViewController() as? WebViewViewController
            let title = controller?.titleString()
            replyHandler(title,nil)
        } else if message.name == "hasNaviTitle" {
            let controller = currentViewController() as? WebViewViewController
            let title = controller?.titleString() ?? ""
            let hasTitle = title.count > 0 ? true : false
            replyHandler(hasTitle,nil)
        } else if message.name == "getNaviOverlay" {
            let controller = currentViewController() as? WebViewViewController
            let overlay = controller?.overlay()
            replyHandler(overlay,nil)
        } else if message.name == "getNaviBackgroundColor" {
            let controller = currentViewController() as? WebViewViewController
            let color = controller?.naviViewBackgroundColor()
            replyHandler(color,nil)
        } else if message.name == "getNaviForegroundColor" {
            let controller = currentViewController() as? WebViewViewController
            let color = controller?.naviViewForegroundColor()
            replyHandler(color,nil)
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
