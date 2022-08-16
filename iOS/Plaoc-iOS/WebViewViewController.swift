//
//  WebViewViewController.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/20.
//

import UIKit
import WebKit
import UIColor_Hex_Swift

class WebViewViewController: UIViewController {

    var urlString: String = ""
    private var isNaviHidden: Bool = false
    private var isBottomHidden: Bool = false
    private var isStatusHidden: Bool = false
    private var naviOverlay: Int = 0
    private var statusOverlay: Int = 0
    private var bottomOverlay: Int = 0
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
        self.view.addSubview(naviView)
        self.view.addSubview(statusView)
//        self.view.addSubview(bottomView)
        
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .white
        
        webView.openWebView(html: "iosqmkkx:/index.html")
//        if urlString.hasPrefix("http") || urlString.hasPrefix("https") {
//            webView.openWebView(html: urlString)
//        } else {
//            webView.openLocalWebView(name: urlString)
//        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
//        webView.openWebView(html: "iosqmkkx:/index.html")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        let str = NSString(string: Bundle.main.bundlePath)
        let path = str.appendingPathComponent("resource_3rd/assets/www")
//        Schemehandler.setupHTMLCache(fromPath: path)
        
//        webView.recycleWebView()
        
//        webView.removeUserScripts()
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
        let webView = CustomWebView(frame: CGRect(x: 0, y: self.naviView.frame.maxY, width: self.view.bounds.width, height: UIScreen.main.bounds.height - self.naviView.frame.maxY - 49 - UIDevice.current.tabbarSpaceHeight()), jsNames: ["Photo","DWebViewJS"])
        webView.superVC = self
        webView.delegate = self
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
    //更新naviView的是否隐藏
    func hiddenNavigationBar(isHidden: Bool) {
        guard isNaviHidden != isHidden else { return }
        isNaviHidden = isHidden
        naviView.isHidden = isHidden
        
        guard naviOverlay == 0 else { return }
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
    //更新naviView的Overlay
    func updateNavigationBarOverlay(overlay: Int) {
        guard naviOverlay != overlay else { return }
        naviOverlay = overlay
        guard !isNaviHidden else { return }
        var frame = webView.frame
        if overlay == 1 {
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
    //更新naviView的背景色
    func updateNavigationBarBackgroundColor(colorString: String) {
        naviView.backgroundColor = UIColor(colorString)
 
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
    //更新naviView的前景色
    func updateNavigationBarTintColor(colorString: String) {
        naviView.tineColor = colorString
    }
    //设置naviView的按钮
    func fetchCustomButtons(buttons: [ButtonModel]) {
        naviView.buttons = buttons
    }
    //返回naviView的标题
    func titleString() -> String {
        return naviView.titleString ?? ""
    }
    //返回naviViewOverlay
    func getNaviOverlay() -> Bool {
        return naviOverlay == 0 ? false : true
    }
    //返回naviView的背景色
    func naviViewBackgroundColor() -> String {
        return naviView.backgroundColor?.hexString() ?? "#FFFFFFFF"
    }
    //返回naviView是否隐藏
    func naviHidden() -> Bool {
        return naviView.isHidden
    }
    //返回naviView的前景色
    func naviViewForegroundColor() -> String {
        return naviView.tineColor ?? ""
    }
    //返回naviView的按钮
    func naviActions() -> String {
        guard let buttons = naviView.buttons else { return "" }
        var array: [[String:Any]] = []
        for button in buttons {
            array.append(button.buttonDict)
        }
        let actionString = ChangeTools.arrayValueString(array) ?? ""
        return actionString
    }
}

// statusBar和js的交互
extension WebViewViewController {
    //更新状态栏背景色
    func updateStatusBackgroundColor(colorString: String) {
        statusView.backgroundColor = UIColor(colorString)
    }
    //更新状态栏状态
    func updateStatusStyle(style: String) {
        if style == "default" {
            self.style = .default
        } else {
            self.style = .lightContent
        }
        setNeedsStatusBarAppearanceUpdate()
    }
    //状态栏是否隐藏
    func updateStatusHidden(isHidden: Bool) {
        isStatusHidden = isHidden
        setNeedsStatusBarAppearanceUpdate()
    }
    //更新状态栏Overlay
    func updateStatusBarOverlay(overlay: Int) {
        statusOverlay = overlay
        var naviFrame = naviView.frame
        var webFrame = webView.frame
        if overlay == 1 {
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
    //返回状态栏是否隐藏
    func statusBarVisible() -> Bool {
        return !isStatusHidden
    }
    //返回状态栏Overlay
    func statusBarOverlay() -> Bool {
        return statusOverlay == 0 ? false : true
    }
    //返回状态栏状态
    func statusBarStyle() -> String {
        if style == .default {
            return "default"
        } else {
            return "lightContent"
        }
    }
    
    func statusBackgroundColor() -> String {
        return statusView.backgroundColor?.hexString() ?? "#FFFFFFFF"
    }
}
// bottomBar和js的交互
extension WebViewViewController {
    //隐藏底部
    func hiddenBottomView(isHidden: Bool) {
        guard isBottomHidden != isHidden else { return }
        isBottomHidden = isHidden
        bottomView.isHidden = isHidden
        guard bottomOverlay == 0 else { return }
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
    //更新底部overlay
    func updateBottomViewOverlay(overlay: Int) {
        guard bottomOverlay != overlay else { return }
        bottomOverlay = overlay
        
        guard !isBottomHidden else { return }
        var frame = webView.frame
        
        if overlay == 1 {
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
    //更新底部背景色
    func updateBottomViewBackgroundColor(colorString: String) {
        bottomView.backgroundColor = UIColor(colorString)
    }
    //更新底部颜色
    func updateBottomViewforegroundColor(colorString: String) {
        //TODO
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
    //隐藏底部按钮
    func hiddenBottomViewButton(hiddenString: String) {
        bottomView.hiddenBtn = hiddenString == "1" ? false : true
    }
    //返回底部是否隐藏
    func bottombarEnabled() -> Bool{
        return isBottomHidden
    }
    //返回底部overlay
    func bottombarOverlay() -> Bool {
        return bottomOverlay == 0 ? false : true
    }
    //返回底部背景颜色
    func bottomBarBackgroundColor() -> String {
        return bottomView.backgroundColor?.hexString() ?? "#FFFFFFFF"
    }
    //返回底部高度
    func bottomViewHeight() -> CGFloat {
        return bottomView.frame.height
    }
    //返回底部颜色
    func bottomBarForegroundColor() -> String {
        //TODO
        return "#FFFFFFFF"
    }
    //获取底部按钮
    func fetchBottomButtons(buttons: [BottomBarModel]) {
        
        operateMonitor.tabBarMonitor.onNext(())
//        bottomView.isHidden = false
//        bottomView.buttons = buttons
    }
    //返回底部按钮数组
    func bottomActions() -> String {
        guard let buttons = bottomView.buttons else { return "" }
        var array: [[String:Any]] = []
        for button in buttons {
            array.append(button.buttonDict)
        }
        let actionString = ChangeTools.arrayValueString(array) ?? ""
        return actionString
    }
    
}

extension WebViewViewController: KeyboardProtocol {
    
    //overlay: false 上移， true 不动
    func keyboardOverlay(overlay: Bool, keyboardType: KeyboardType, height: CGFloat) {
        
        guard !isNaviHidden else { return }
        var naviFrame = naviView.frame
        var webFrame = webView.frame
        var bottomFrame = bottomView.frame
        if keyboardType == .show {
            naviFrame.origin.y -= height
            webFrame.origin.y -= height
            bottomFrame.origin.y -= height
        } else if keyboardType == .hidden {
            naviFrame.origin.y = self.statusView.frame.maxY
            webFrame.origin.y = naviFrame.maxY
            bottomFrame.origin.y = UIScreen.main.bounds.height - 49 - UIDevice.current.tabbarSpaceHeight()
        }
        UIView.animate(withDuration: 0.25) {
            self.naviView.frame = naviFrame
            self.webView.frame = webFrame
            self.bottomView.frame = bottomFrame
        }
    }
}
