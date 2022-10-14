//
//  CustomWebView.swift
//  WebViewController
//
//  Created by mac on 2022/3/15.
//

import UIKit
import WebKit
import SwiftyJSON

typealias UpdateTitleCallback = (String) -> Void

enum KeyboardType {
    case show
    case hidden
}

protocol KeyboardProtocol {
    func keyboardOverlay(overlay: Bool, keyboardType: KeyboardType, height: CGFloat)
}

class CustomWebView: UIView {

    var callback: UpdateTitleCallback?
    var delegate: KeyboardProtocol?
    var superVC: UIViewController?
    private var scripts: [WKUserScript]?
    private let imageUrl_key: String = "imageUrl"
    private var isKeyboardOverlay: Bool = false
    private var keyHeight:CGFloat = 0
    private var fileName: String = ""
    
    init(frame: CGRect, jsNames: [String], fileName: String) {
        super.init(frame: frame)
        scripts = addUserScript(jsNames: jsNames)
        self.fileName = fileName
        self.addSubview(webView)
        
        NotificationCenter.default.addObserver(self, selector: #selector(observerShowKeyboard(noti:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(observerHiddenKeyboard(noti:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        
        
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func removeUserScripts() {
        webView.configuration.userContentController.removeAllUserScripts()
        if #available(iOS 14.0, *) {
            webView.configuration.userContentController.removeAllScriptMessageHandlers()
        } else {
            // Fallback on earlier versions
        }
    }
    
    private lazy var webView: WKWebView = {
        
        let config = WKWebViewConfiguration()
        config.userContentController = WKUserContentController()
        if self.scripts != nil {
            for script in self.scripts! {
                config.userContentController.addUserScript(script)
            }
        }
        let prefreen = WKPreferences()
        prefreen.javaScriptCanOpenWindowsAutomatically = true
        config.preferences = prefreen
        config.setValue(true, forKey: "allowUniversalAccessFromFileURLs")
        config.setURLSchemeHandler(Schemehandler(fileName: self.fileName), forURLScheme: schemeString)
        let webView = WKWebView(frame: self.bounds, configuration: config)
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.allowsBackForwardNavigationGestures = true
        if #available(iOS 11.0, *) {
            webView.scrollView.contentInsetAdjustmentBehavior = .never
        } else {

        }
        
//        let webView = WebViewPool.shared.getReusedWebView(forHolder: self)
//        webView.frame = self.bounds
//        webView.uiDelegate = self
//        webView.navigationDelegate = self
//        addScriptMessageHandler(config: webView.configuration)
//        addScriptMessageHandlerWithReply(config: webView.configuration)
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
//            let request = URLRequest(url: url)
            let request = URLRequest(url: url, cachePolicy: .reloadIgnoringLocalAndRemoteCacheData, timeoutInterval: 30)
            self.webView.load(request)
        }
    }
    
    func openLocalWebView(name: String) {

        var path = name
        path = "file://".appending(path)
        if let url = URL(string: path) {
            self.webView.load(URLRequest(url: url, cachePolicy: .reloadIgnoringLocalAndRemoteCacheData, timeoutInterval: 30))
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
    
    @objc private func observerShowKeyboard(noti: Notification) {
        
        guard let keyboardBound = noti.userInfo?["UIKeyboardFrameEndUserInfoKey"] as? CGRect else { return }
        keyHeight = keyboardBound.height
        
        guard !isKeyboardOverlay else { return }
        delegate?.keyboardOverlay(overlay: isKeyboardOverlay, keyboardType: .show, height: keyHeight)
        
        let safeArea = UIEdgeInsets(top: 0, left: 0, bottom: keyboardBound.height, right: 0)
        
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
    @objc private func observerHiddenKeyboard(noti: Notification) {
        let safeArea = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
        
        guard !isKeyboardOverlay else { return }
        delegate?.keyboardOverlay(overlay: isKeyboardOverlay, keyboardType: .hidden, height: keyHeight)
    }
    
    func recycleWebView() {
//        WebViewPool.shared.recycleReusedWebView(webView: webView)
    }
    
    func updateFrame(frame: CGRect) {
        webView.frame = CGRect(origin: .zero, size: frame.size)
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
        let keyboardString = "setHeight('\(49 + UIDevice.current.tabbarSpaceHeight())')"
        handleJavascriptString(inputJS: keyboardString)
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
        let sureAction2 = UIAlertAction(title: "CANCEL", style: .default) { action in
            completionHandler(alert.textFields?.first?.text ?? "")
        }
        alert.addAction(sureAction)
        alert.addAction(sureAction2)
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
