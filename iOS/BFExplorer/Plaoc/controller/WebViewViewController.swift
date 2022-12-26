//
//  WebViewViewController.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/20.
//

import UIKit
import WebKit
import UIColor_Hex_Swift
import SwiftyJSON
import JavaScriptCore

class WebViewViewController: UIViewController {

    var urlString: String = ""
    var fileName: String = ""
    private var isNaviHidden: Bool = false
    private var isStatusHidden: Bool = false
    private var naviOverlay: Bool = true
    private var statusOverlay: Bool = true
    private var bottomOverlay: Bool = true
    private var style: UIStatusBarStyle = .default
    var jsManager: JSCoreManager!
    
    
    private let jsContext = JSContext()
    
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return style
    }
    
    override var prefersStatusBarHidden: Bool {
        return isStatusHidden
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
      
        self.navigationController?.isNavigationBarHidden = true
        
        self.view.addSubview(webView)
        self.view.addSubview(naviView)
        self.view.addSubview(statusView)
        self.view.addSubview(bottomView)
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        jsManager = JSCoreManager.init(fileName: fileName, controller: self)
        webView.jsManager = jsManager
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        NotificationCenter.default.removeObserver(self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .white
        
        webView.openWebView(html: urlString)
        
        NotificationCenter.default.addObserver(self, selector: #selector(interceptAction(noti:)), name: NSNotification.Name.interceptNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(observerShowKeyboard(noti:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(observerHiddenKeyboard(noti:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(openDwebAction(noti:)), name: NSNotification.Name.openDwebNotification, object: nil)
        
    }
    
    @objc private func openDwebAction(noti: Notification) {
//        guard let info = noti.userInfo as? [String:String] else { return }
//        guard let urlString = info["param"] else { return }
//        webView.openWebView(html: "http://www.baidu.com")
    }
    
    @objc private func interceptAction(noti: Notification) {
        let info = noti.userInfo as? [String:Any]
        let function = info?["function"] as? String

        switch function {
        case "getTopBarShow":
            let isShowString = getNaviHiddenState() ? "true" : "false"
            rewriteUrlSchemeTaskResponse(info: info, content: isShowString)
        case "SetTopBarEnabled":
            let param = info?["param"] as? Bool
            hiddenNavigationBar(isHidden: param ?? false)
        case "GetTopBarOverlay":
            let overlay = naviViewOverlay()
            rewriteUrlSchemeTaskResponse(info: info, content: overlay)
        case "SetTopBarOverlay":
            guard let param = info?["param"] as? Bool else { return }
            updateNavigationBarOverlay(overlay: param)
        case "GetTopBarAlpha":
            let alpha = naviViewAlpha()
            rewriteUrlSchemeTaskResponse(info: info, content: alpha)
        case "SetTopBarAlpha":
            guard let param = info?["param"] as? Double else { return }
            setNaviViewAlpha(alpha: param)
        case "GetTopBarTitle":
            let title = titleString()
            rewriteUrlSchemeTaskResponse(info: info, content: title)
        case "SetTopBarTitle":
            let param = info?["param"] as? String
            setNaviViewTitle(title: param)
        case "HasTopBarTitle":
            let title = titleString()
            let isHas = title.count > 0 ? "true" : "false"
            rewriteUrlSchemeTaskResponse(info: info, content: isHas)
        case "GetTopBarHeight":
            let height = naviViewHeight()
            rewriteUrlSchemeTaskResponse(info: info, content: height)
        case "GetTopBarActions":
            let actions = naviActions()
            rewriteUrlSchemeTaskResponse(info: info, content: actions)
        case "SetTopBarActions":
            guard let param = info?["param"] as? String else { return }
            setNaviButtons(content: param)
        case "GetTopBarBackgroundColor":
            let backgroundColor = naviViewBackgroundColor()
            rewriteUrlSchemeTaskResponse(info: info, content: backgroundColor)
        case "SetTopBarBackgroundColor":
            guard let param = info?["param"] as? Int else { return }
            updateNavigationBarBackgroundColor(colorString: "\(param)")
        case "GetTopBarForegroundColor":
            let foreColor = naviViewForegroundColor()
            rewriteUrlSchemeTaskResponse(info: info, content: foreColor)
        case "SetTopBarForegroundColor":
            guard let param = info?["param"] as? Int else { return }
            updateNavigationBarTintColor(colorString: "\(param)")
        case "SetStatusBarColor":
            guard let param = info?["param"] as? Int else { return }
            updateStatusBackgroundColor(colorString: "\(param)")
        case "GetStatusBarColor":
            let backgroundColor = statusBackgroundColor()
            rewriteUrlSchemeTaskResponse(info: info, content: backgroundColor)
        case "GetStatusBarIsDark":
            let style = statusBarStyle()
            rewriteUrlSchemeTaskResponse(info: info, content: style)
        case "SetStatusBarVisible":
            guard let param = info?["param"] as? Bool else { return }
            updateStatusHidden(isHidden: param)
        case "GetStatusBarVisible":
            let visible = statusBarVisible()
            rewriteUrlSchemeTaskResponse(info: info, content: visible)
        case "SetStatusBarOverlay":
            guard let param = info?["param"] as? Bool else { return }
            updateStatusBarOverlay(overlay: param)
        case "GetStatusBarOverlay":
            let visible = statusBarOverlay()
            rewriteUrlSchemeTaskResponse(info: info, content: visible)
        case "SetBottomBarEnabled":
            guard let param = info?["param"] as? Bool else { return }
            hiddenBottomView(isHidden: param)
        case "GetBottomBarEnabled":
            let visible = bottombarHidden()
            rewriteUrlSchemeTaskResponse(info: info, content: visible ? "true" : "false")
        case "SetBottomBarOverlay":
            guard let param = info?["param"] as? Bool else { return }
            updateBottomViewOverlay(overlay: param)
        case "GetBottomBarOverlay":
            let overlay = bottombarOverlay()
            rewriteUrlSchemeTaskResponse(info: info, content: overlay ? "true":"false")
        case "GetBottomBarAlpha":
            let alpha = bottomViewAlpha()
            rewriteUrlSchemeTaskResponse(info: info, content: "\(alpha)")
        case "SetBottomBarAlpha":
            guard let param = info?["param"] as? Double else { return }
            setBottomViewAlpha(alpha: param)
        case "SetBottomBarHeight":
            guard let param = info?["param"] as? Double else { return }
            updateBottomViewHeight(height: param)
        case "GetBottomBarHeight":
            let height = bottomViewHeight()
            rewriteUrlSchemeTaskResponse(info: info, content: "\(height)")
        case "SetBottomBarActions":
            guard let param = info?["param"] as? String else { return }
            fetchBottomButtons(content: param)
        case "GetBottomBarActions":
            let actions = bottomActions()
            rewriteUrlSchemeTaskResponse(info: info, content: actions)
        case "SetBottomBarBackgroundColor":
            guard let param = info?["param"] as? String else { return }
            updateBottomViewBackgroundColor(colorString: param)
        case "GetBottomBarBackgroundColor":
            let color = bottomBarBackgroundColor()
            rewriteUrlSchemeTaskResponse(info: info, content: color)
        case "SetBottomBarForegroundColor":
            guard let param = info?["param"] as? String else { return }
            updateBottomViewforegroundColor(colorString: param)
        case "GetBottomBarForegroundColor":
            let color = bottomBarForegroundColor()
            rewriteUrlSchemeTaskResponse(info: info, content: color)
        case "OpenDialogAlert":
            guard let param = info?["param"] as? String else { return }
            openAlertAction(info: info, content: param)
        case "OpenDialogPrompt":
            guard let param = info?["param"] as? String else { return }
            openPromptAction(info: info, content: param)
        case "OpenDialogConfirm":
            guard let param = info?["param"] as? String else { return }
            openConfirmAction(info: info, content: param)
        case "OpenDialogWarning":
            //TODO 不确定具体界面
            break
        case "OpenQrScanner":    //二维码
            let scanVC = ScanPhotoViewController()
            self.navigationController?.pushViewController(scanVC, animated: true)
        case "BarcodeScanner":   //条形码
            let scanVC = ScanPhotoViewController()
            self.navigationController?.pushViewController(scanVC, animated: true)
        default:
            break
        }
    }
    
    @objc private func observerShowKeyboard(noti: Notification) {
        
        guard let keyboardBound = noti.userInfo?["UIKeyboardFrameEndUserInfoKey"] as? CGRect else { return }
        
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
        
    }
    
    //拦截后，重新把数据写入请求
    private func rewriteUrlSchemeTaskResponse(info: [String:Any]?, content: String) {
        guard let urlSchemeTask = info?["scheme"] as? WKURLSchemeTask else { return }
        guard urlSchemeTask.request.url != nil else { return }
        guard let data = content.data(using: .utf8) else { return }
        let type = "text/html"
        let response = URLResponse(url: urlSchemeTask.request.url!, mimeType: type, expectedContentLength: data.count, textEncodingName: nil)
        urlSchemeTask.didReceive(response)
        urlSchemeTask.didReceive(data)
        urlSchemeTask.didFinish()
    }

    lazy var statusView: StatusView = {
        let statusView = StatusView(frame: CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIDevice.current.statusBarHeight()))
        return statusView
    }()
    
    lazy var naviView: NaviView = {
        let naviView = NaviView(frame: CGRect(x: 0, y: self.statusView.frame.maxY, width: UIScreen.main.bounds.width, height: 44))
        naviView.callback = { [weak self] code in
            guard let strongSelf = self else { return }
            strongSelf.webView.handleJavascriptString(inputJS: code)
        }
        return naviView
    }()
    
    lazy private var webView: CustomWebView = {
        let webView = CustomWebView(frame: CGRect(x: 0, y: 44, width: self.view.bounds.width, height: UIScreen.main.bounds.height - 44), jsNames: ["install"], fileName: fileName, urlString: self.urlString)
        webView.superVC = self
        webView.callback = { [weak self] title in
            guard let strongSelf = self else { return }
            strongSelf.naviView.titleString = title
        }
        return webView
    }()
    
    lazy var bottomView: BottomView = {
        let bottomView = BottomView(frame: CGRect(x: 0, y: UIScreen.main.bounds.height - 49 - UIDevice.current.tabbarSpaceHeight(), width: UIScreen.main.bounds.width, height: 49 + UIDevice.current.tabbarSpaceHeight()))
//        bottomView.isHidden = true
        bottomView.callback = { [weak self] code in
            guard let strongSelf = self else { return }
            strongSelf.webView.handleJavascriptString(inputJS: code)
        }
        return bottomView
    }()

}

// naviBar和js的交互
extension WebViewViewController {
    //更新naviView的是否隐藏
    private func hiddenNavigationBar(isHidden: Bool) {
        naviView.hiddenNavigationView(hidden: isHidden)
    }
    //返回naviView是否隐藏
    private func getNaviHiddenState() -> Bool {
        return naviView.naviHiddenState()
    }
    
    //更新naviView的Overlay
    func updateNavigationBarOverlay(overlay: Bool) {
        guard naviView.naviOverlay != overlay else { return }
        naviView.updateNavigationBarOverlay(overlay: overlay)
        var frame = webView.frame
        if overlay {
            frame.origin.y = UIDevice.current.statusBarHeight() + 44
            frame.size.height -= 44
        } else {
            frame.origin.y = UIDevice.current.statusBarHeight()
            frame.size.height += 44
        }
        UIView.animate(withDuration: 0.25) {
            self.webView.frame = frame
            self.webView.updateFrame(frame: frame)
        }
    }
    //获取naviView的Overlay
    private func naviViewOverlay() -> String {
        return naviView.naviViewOverlay()
    }
    
    //更新naviView的背景色
    private func updateNavigationBarBackgroundColor(colorString: String) {
        naviView.updateNavigationBarBackgroundColor(colorString: colorString)
    }
    //返回naviView的背景色
    private func naviViewBackgroundColor() -> String {
        return naviView.backgroundColorString()
    }
    //更新naviView的前景色
    private func updateNavigationBarTintColor(colorString: String) {
        naviView.updateNavigationBarTintColor(colorString: colorString)
    }
    //返回naviView的前景色
    private func naviViewForegroundColor() -> String {
        return naviView.foregroundColor()
    }
    
    //设置标题
    private func setNaviViewTitle(title: String?) {
        naviView.setNaviViewTitle(title: title)
    }
    //返回naviView的标题
    private func titleString() -> String {
        return naviView.titleContent()
    }
    //naviView的高度
    private func naviViewHeight() -> String {
        return naviView.viewHeight()
    }
    
    //naviView透明度
    private func naviViewAlpha() -> String {
        return naviView.viewAlpha()
    }
    //设置naviView透明度
    private func setNaviViewAlpha(alpha: CGFloat) {
        naviView.setNaviViewAlpha(alpha: alpha)
    }
    
    //设置naviView的按钮
    private func setNaviButtons(content: String) {
        naviView.setNaviButtons(content: content)
    }
    //返回naviView的按钮
    private func naviActions() -> String {
        return naviView.naviActions()
    }
}

// statusBar和js的交互
extension WebViewViewController {
    //更新状态栏背景色
    private func updateStatusBackgroundColor(colorString: String) {
        statusView.backgroundColor = UIColor(colorString)
    }
    //状态栏背景色
    private func statusBackgroundColor() -> String {
        return statusView.backgroundColor?.hexString() ?? "#FFFFFFFF"
    }
    
    //更新状态栏状态
    private func updateStatusStyle(style: String) {
        if style == "default" {
            self.style = .default
        } else {
            self.style = .lightContent
        }
        setNeedsStatusBarAppearanceUpdate()
    }
    //返回状态栏状态
    private func statusBarStyle() -> String {
        if style == .default {
            return "true"
        } else {
            return "false"
        }
    }
    
    //状态栏是否隐藏
    private func updateStatusHidden(isHidden: Bool) {
        isStatusHidden = isHidden
        setNeedsStatusBarAppearanceUpdate()
    }
    //返回状态栏是否隐藏
    private func statusBarVisible() -> String {
        return isStatusHidden ? "true" : "false"
    }
    
    //更新状态栏Overlay
    private func updateStatusBarOverlay(overlay: Bool) {
        guard statusOverlay != overlay else { return }
        statusOverlay = overlay
        var naviFrame = naviView.frame
        var webFrame = webView.frame
        if overlay {
            naviFrame.origin.y -= UIDevice.current.statusBarHeight()
            webFrame.size.height += UIDevice.current.statusBarHeight()
        } else {
            naviFrame.origin.y = UIDevice.current.statusBarHeight()
            webFrame.size.height -= UIDevice.current.statusBarHeight()
        }
        webFrame.origin.y = naviFrame.maxY
        UIView.animate(withDuration: 0.25) {
            self.naviView.frame = naviFrame
            self.webView.frame = webFrame
            self.webView.updateFrame(frame: webFrame)
        }
    }
    
    //返回状态栏Overlay
    private func statusBarOverlay() -> String {
        return statusOverlay ? "true" : "false"
    }
}
// bottomBar和js的交互
extension WebViewViewController {
    //隐藏底部
    private func hiddenBottomView(isHidden: Bool) {
        bottomView.hiddenBottomView(hidden: isHidden)
    }
    //返回底部是否隐藏
    private func bottombarHidden() -> Bool {
        return bottomView.bottomHiddenState()
    }
    
    //更新底部overlay
    private func updateBottomViewOverlay(overlay: Bool) {
        guard bottomView.bottomViewOverlay() != overlay else { return }
        bottomView.updateBottomViewOverlay(overlay: overlay)
        
        var frame = webView.frame
        
        if overlay {
            frame.size.height += 49 + UIDevice.current.tabbarSpaceHeight()
        } else {
            frame.size.height -= 49 + UIDevice.current.tabbarSpaceHeight()
        }
       
        frame = CGRect(x: 0, y: self.naviView.frame.maxY, width: self.view.bounds.width, height: UIScreen.main.bounds.height - self.naviView.frame.maxY - 49 - UIDevice.current.tabbarSpaceHeight() + 100)
        UIView.animate(withDuration: 0.25) {
            self.webView.frame = frame
            self.webView.updateFrame(frame: frame)
        }
    }
    //返回底部overlay
    private func bottombarOverlay() -> Bool {
        return bottomView.bottomViewOverlay()
    }
    //设置底部alpha
    private func setBottomViewAlpha(alpha: CGFloat) {
        bottomView.updaterBottomViewAlpha(alpha: alpha)
    }
    //返回底部alpha
    private func bottomViewAlpha() -> CGFloat {
        return bottomView.bottomViewAlpha()
    }
    
    //更新底部背景色
    private func updateBottomViewBackgroundColor(colorString: String) {
        bottomView.updateBottomViewBackgroundColor(colorString: colorString)
    }
    //返回底部背景颜色
    private func bottomBarBackgroundColor() -> String {
        return bottomView.bottomBarBackgroundColor()
    }
    
    //更新底部颜色
    private func updateBottomViewforegroundColor(colorString: String) {
        bottomView.updateBottomViewforegroundColor(colorString: colorString)
    }
    //返回底部颜色
    private func bottomBarForegroundColor() -> String {
        return bottomView.bottomBarForegroundColor()
    }
    
    //更新底部高度
    func updateBottomViewHeight(height: CGFloat) {
        var frame = bottomView.frame
        frame.size.height = height
        frame.origin.y = UIScreen.main.bounds.height - height
        bottomView.frame = frame
        UIView.animate(withDuration: 0.25) {
            self.bottomView.frame = frame
        }
    }
    //返回底部高度
    private func bottomViewHeight() -> CGFloat {
        return bottomView.bottomViewHeight()
    }
    
    //隐藏底部按钮
    private func hiddenBottomViewButton(hidden: Bool) {
        bottomView.hiddenBottomViewButton(hidden: hidden)
    }
    
    //获取底部按钮
    private func fetchBottomButtons(content: String) {
        bottomView.fetchBottomButtons(content: content)
    }
    //返回底部按钮数组
    private func bottomActions() -> String {
        return bottomView.bottomActions()
    }
}

extension WebViewViewController {
    
    private func openAlertAction(info: [String:Any]?, content: String) {
        guard let bodyDict = ChangeTools.stringValueDic(content) else { return }
        let configString = bodyDict["config"] as? String
        let cbString = bodyDict["cb"] as? String
        let configDict = ChangeTools.stringValueDic(configString ?? "")
        let alertModel = AlertConfiguration(dict: JSON(configDict))
        let alertView = CustomAlertPopView(frame: CGRect(x: 0, y: 0, width: screen_width, height: screen_height))
        alertView.alertModel = alertModel
        alertView.callback = { [weak self] type in
            guard let strongSelf = self else { return }
            guard cbString != nil, cbString!.count > 0 else { return }
            let jsString = cbString! + "(\(true))"
            guard jsString.count > 0 else { return }
            strongSelf.webView.handleJavascriptString(inputJS: jsString)
        }
        alertView.show()
    }
    
    private func openPromptAction(info: [String:Any]?, content: String) {
        guard let bodyDict = ChangeTools.stringValueDic(content) else { return }
        let configString = bodyDict["config"] as? String
        let cbString = bodyDict["cb"] as? String
        let configDict = ChangeTools.stringValueDic(configString ?? "")
        let promptModel = PromptConfiguration(dict: JSON(configDict))
        let alertView = CustomPromptPopView(frame: CGRect(x: 0, y: 0, width: screen_width, height: screen_height))
        alertView.promptModel = promptModel
        alertView.callback = { [weak self] type in
            guard let strongSelf = self else { return }
            guard cbString != nil, cbString!.count > 0 else { return }
            var jsString: String = ""
            if type == .confirm {
                jsString = cbString! + "(\"\(alertView.textField.text ?? "")\")"
            } else if type == .cancel {
                jsString = cbString! + "(\(false))"
            }
            guard jsString.count > 0 else { return }
            strongSelf.webView.handleJavascriptString(inputJS: jsString)
        }
        alertView.show()
    }
    
    private func openConfirmAction(info: [String:Any]?, content: String) {
        guard let bodyDict = ChangeTools.stringValueDic(content) else { return }
        let configString = bodyDict["config"] as? String
        let cbString = bodyDict["cb"] as? String
        let configDict = ChangeTools.stringValueDic(configString ?? "")
        let confirmModel = ConfirmConfiguration(dict: JSON(configDict))
        let alertView = CustomConfirmPopView(frame: CGRect(x: 0, y: 0, width: screen_width, height: screen_height))
        alertView.confirmModel = confirmModel
        alertView.callback = { [weak self] type in
            guard let strongSelf = self else { return }
            guard cbString != nil, cbString!.count > 0 else { return }
            var jsString: String = ""
            if type == .confirm {
                jsString = cbString! + "(\(true))"
            } else if type == .cancel {
                jsString = cbString! + "(\(false))"
            }
            guard jsString.count > 0 else { return }
            strongSelf.webView.handleJavascriptString(inputJS: jsString)
        }
        alertView.show()
    }
    
    private func openBeforeUnloadAction(info: [String:Any]?, content: String) {
        guard let bodyDict = ChangeTools.stringValueDic(content) else { return }
        let confirmModel = ConfirmConfiguration(dict: JSON(bodyDict))
        let alertView = CustomConfirmPopView(frame: CGRect(x: 0, y: 0, width: screen_width, height: screen_height))
        alertView.confirmModel = confirmModel
        alertView.callback = { [weak self] type in
            guard let strongSelf = self else { return }
            var jsString: String = ""
            if type == .confirm {
                jsString = confirmModel.confirmFunc ?? ""
            } else if type == .cancel {
                jsString = confirmModel.cancelFunc ?? ""
            }
            guard jsString.count > 0 else { return }
            strongSelf.rewriteUrlSchemeTaskResponse(info: info, content: jsString)
        }
        alertView.show()
    }
}
