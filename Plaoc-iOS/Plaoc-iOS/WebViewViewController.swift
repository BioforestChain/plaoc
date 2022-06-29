//
//  WebViewViewController.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/20.
//

import UIKit
import WebKit

class WebViewViewController: UIViewController {

    var urlString: String = ""
    private var isNaviHidden: Bool = false
    private var isBottomHidden: Bool = false
    private var isStatusHidden: Bool = false
    private var isNaviAlpha: Bool = false
    private var isStatusAlpha: Bool = false
    private var isBottomAlpha: Bool = false
    private var naviBackgroundColorDict: [String:Any] = [:]
    private var bottomViewBackgroundColorDict: [String:Any] = [:]
    private var bottomViewForegroundColorDict: [String:Any] = [:]
    
    private var style: UIStatusBarStyle = .default
    
    
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
        self.view.addSubview(statusView)
        self.view.addSubview(naviView)
        self.view.addSubview(bottomView)
        
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .white
        
        webView.openWebView(html: urlString)
//        if urlString.hasPrefix("http") || urlString.hasPrefix("https") {
//            webView.openWebView(html: urlString)
//        } else {
//            webView.openLocalWebView(name: urlString)
//        }
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
    
    lazy var webView: CustomWebView = {
        let webView = CustomWebView(frame: CGRect(x: 0, y: self.naviView.frame.maxY, width: self.view.bounds.width, height: self.view.bounds.height - self.naviView.frame.maxY), jsNames: ["Photo","DWebViewJS"])
        webView.superVC = self
        webView.callback = { [weak self] title in
            guard let strongSelf = self else { return }
            strongSelf.naviView.titleString = title
        }
        return webView
    }()
    
    lazy var bottomView: BottomView = {
        let bottomView = BottomView(frame: CGRect(x: 0, y: UIScreen.main.bounds.height - 49 - UIDevice.current.tabbarSpaceHeight(), width: UIScreen.main.bounds.width, height: 49 + UIDevice.current.tabbarSpaceHeight()))
        bottomView.isHidden = true
        bottomView.callback = { [weak self] code in
            guard let strongSelf = self else { return }
            strongSelf.webView.handleJavascriptString(inputJS: code)
        }
        return bottomView
    }()

}

// naviBar和js的交互
extension WebViewViewController {
    
    func hiddenNavigationBar(isHidden: Bool) {
        guard isNaviHidden != isHidden else { return }
        isNaviHidden = isHidden
        naviView.isHidden = isHidden
        
        guard !isNaviAlpha else { return }
        var frame = webView.frame
        
        if isNaviHidden {
            frame.origin.y = UIDevice.current.statusBarHeight()
            frame.size.height += 44
        } else {
            frame.origin.y = UIDevice.current.statusBarHeight() + 44
            frame.size.height -= 44
        }
       
        UIView.animate(withDuration: 0.25) {
            self.webView.frame = frame
            self.webView.updateFrame(frame: frame)
        }
    }
    
    func updateNavigationBarAlpha(isAlpha: Bool) {
        guard isNaviAlpha != isAlpha else { return }
        isNaviAlpha = isAlpha
        naviView.alpha = isAlpha ? 0.5 : 1.0
        guard !isNaviHidden else { return }
        var frame = webView.frame
        if isNaviAlpha {
            frame.origin.y = UIDevice.current.statusBarHeight()
            frame.size.height += 44
        } else {
            frame.origin.y = UIDevice.current.statusBarHeight() + 44
            frame.size.height -= 44
        }
        UIView.animate(withDuration: 0.25) {
            self.webView.frame = frame
            self.webView.updateFrame(frame: frame)
        }
    }
    
    func updateNavigationBarBackgroundColor(colorDict: [String:Any]) {
        naviBackgroundColorDict = colorDict
        if let color = colorDict["color"] as? String {
            naviView.backgroundColor = UIColor(hexString: color)
            if let alpha = colorDict["alpha"] as? Float {
                naviView.backgroundColor = UIColor(hexString: color).withAlphaComponent(CGFloat(alpha))
            }
        }
        
//        self.navigationController?.navigationBar.backgroundColor = .orange
//        let image = createImageWithColor(.orange, frame: CGRect(x: 0, y: 0.5, width: 1, height: 0.5))
//        self.navigationController?.navigationBar.setBackgroundImage(image, for: .default)
        
    }
    
     //生成一个指定颜色的图片
    private func createImageWithColor(_ color: UIColor, frame: CGRect) -> UIImage? {
        // 开始绘图
        UIGraphicsBeginImageContext(frame.size)
         
        // 获取绘图上下文
        let context = UIGraphicsGetCurrentContext()
        // 设置填充颜色
        context?.setFillColor(color.cgColor)
        // 使用填充颜色填充区域
        context?.fill(frame)
         
        // 获取绘制的图像
        let image = UIGraphicsGetImageFromCurrentImageContext()
         
        // 结束绘图
        UIGraphicsEndImageContext()
        return image
    }
    
    func updateNavigationBarTintColor(colorDict: [String:Any]) {
        if let alpha = colorDict["alpha"] as? Float {
            naviView.tineAlpha = CGFloat(alpha)
        }
        if let color = colorDict["color"] as? String {
            naviView.tineColor = color
        }
    }
    
    func fetchCustomButtons(buttons: [ButtonModel]) {
        naviView.buttons = buttons.reversed()
    }
    
    func titleString() -> String {
        return naviView.titleString ?? ""
    }
    
    func overlay() -> Bool {
        return isNaviAlpha
    }
    
    func naviViewBackgroundColor() -> [String:Any] {
        return naviBackgroundColorDict
    }
    
    func naviHidden() -> Bool {
        return naviView.isHidden
    }
    
    func naviViewForegroundColor() -> [String:Any] {
        var dict: [String:Any] = [:]
        if let alpha = naviView.tineAlpha {
            dict["alpha"] = alpha
        }
        if let color = naviView.tineColor {
            dict["color"] = color
        }
        return dict
    }
    
    func naviActions() -> [[String:Any]] {
        guard let buttons = naviView.buttons else { return [] }
        var array: [[String:Any]] = []
        for button in buttons {
            array.append(button.buttonDict)
        }
        return array
    }
}

// statusBar和js的交互
extension WebViewViewController {
    
    func updateStatusBackgroundColor(dict: [String:Any]) {
        if let color = dict["colorHex"] as? String {
            statusView.backgroundColor = UIColor(hexString: color)
        }
        if let alpha = dict["alpha"] as? CGFloat {
            statusView.alpha = alpha
        }
    }
    
    func updateStatusStyle() {
        if self.style == .default {
            self.style = .lightContent
        } else {
            self.style = .default
        }
        setNeedsStatusBarAppearanceUpdate()
    }
    
    func updateStatusHidden(isHidden: Bool) {
        isStatusHidden = isHidden
        setNeedsStatusBarAppearanceUpdate()
    }
    
    func updateStatusBarAlpha(isOverlay: Bool) {
        isStatusAlpha = isOverlay
        statusView.alpha = isStatusAlpha ? 0.5 : 1.0
    }
    
    func statusBarVisible() -> Bool {
        return isStatusHidden
    }
    
    func statusBarOverlay() -> Bool {
        return isStatusAlpha
    }
}
// bottomBar和js的交互
extension WebViewViewController {
    
    func hiddenBottomView(isHidden: Bool) {
        guard isBottomHidden != isHidden else { return }
        isBottomHidden = isHidden
        bottomView.isHidden = isHidden
        guard !isBottomAlpha else { return }
        var frame = webView.frame
        
        if isBottomHidden {
            frame.size.height += 49 + UIDevice.current.tabbarSpaceHeight()
        } else {
            frame.size.height -= 49 + UIDevice.current.tabbarSpaceHeight()
        }
       
        UIView.animate(withDuration: 0.25) {
            self.webView.frame = frame
            self.webView.updateFrame(frame: frame)
        }
    }
    
    func updateBottomViewAlpha(isAlpha: Bool) {
        guard isBottomAlpha != isAlpha else { return }
        isBottomAlpha = isAlpha
        bottomView.alpha = isAlpha ? 0.5 : 1.0
        
        guard !isBottomHidden else { return }
        var frame = webView.frame
        
        if isBottomAlpha {
            frame.size.height += 49 + UIDevice.current.tabbarSpaceHeight()
        } else {
            frame.size.height -= 49 + UIDevice.current.tabbarSpaceHeight()
        }
       
        UIView.animate(withDuration: 0.25) {
            self.webView.frame = frame
            self.webView.updateFrame(frame: frame)
        }
    }
    
    func updateBottomViewBackgroundColor(dict: [String:Any]) {
        bottomViewBackgroundColorDict = dict
        if let color = dict["color"] as? String {
            bottomView.backgroundColor = UIColor(hexString: color)
            if let alpha = dict["alpha"] as? Float {
                bottomView.backgroundColor = UIColor(hexString: color).withAlphaComponent(CGFloat(alpha))
            }
        }
    }
    
    func updateBottomViewforegroundColor(dict: [String:Any]) {
        //TODO
        bottomViewForegroundColorDict = dict
    }
    
    func updateBottomViewHeight(height: CGFloat) {
        var frame = bottomView.frame
        frame.size.height = height
        frame.origin.y = UIScreen.main.bounds.height - height
        bottomView.frame = frame
        UIView.animate(withDuration: 0.25) {
            self.bottomView.frame = frame
        }
    }
    
    func hiddenBottomViewButton(hiddenString: String) {
        bottomView.hiddenBtn = hiddenString == "1" ? false : true
    }
    
    func bottombarEnabled() -> Bool{
        return isBottomHidden
    }
    
    func bottombarOverlay() -> Bool {
        return isBottomAlpha
    }
    
    func bottomBarBackgroundColor() -> [String:Any] {
        return bottomViewBackgroundColorDict
    }
    
    func bottomViewHeight() -> CGFloat {
        return bottomView.frame.height
    }
    
    func bottomBarForegroundColor() -> [String:Any] {
        //TODO
        return bottomViewForegroundColorDict
    }
    
    func fetchBottomButtons(buttons: [BottomBarModel]) {
        bottomView.isHidden = false
        bottomView.buttons = buttons
    }
    
    func bottomActions() -> [[String:Any]] {
        guard let buttons = bottomView.buttons else { return [] }
        var array: [[String:Any]] = []
        for button in buttons {
            array.append(button.buttonDict)
        }
        return array
    }
    
}
