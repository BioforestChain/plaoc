//
//  WebViewViewController.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/20.
//

import UIKit

class WebViewViewController: UIViewController {

    var urlString: String = ""
    private var isNaviHidden: Bool = false
    private var isBottomHidden: Bool = true
    private var isStatusHidden: Bool = false
    private var isNaviAlpha: Bool = false
    private var isStatusAlpha: Bool = false
    private var isBottomAlpha: Bool = false
    
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
        
        if urlString.hasPrefix("http") || urlString.hasPrefix("https") {
            webView.openWebView(html: urlString)
        } else {
            webView.openLocalWebView(name: urlString)
        }
    }
    

    lazy var statusView: StatusView = {
        let statusView = StatusView(frame: CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIDevice.current.statusBarHeight()))
        return statusView
    }()
    
    lazy var naviView: NaviView = {
        let naviView = NaviView(frame: CGRect(x: 0, y: self.statusView.frame.maxY, width: UIScreen.main.bounds.width, height: 44))
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
        return bottomView
    }()

}

extension WebViewViewController {
    
    func updateTopBarBackgroundColor() {
        updateNavigationBarBackgroundColor()
        updateStatusBackgroundColor()
    }
    
    func updateTopBarForegroundColor() {
        updateNavigationBarTintColor()
    }
    
    func updateTopBarAlpha() {
        updateNavigationBarAlpha()
    }
    
    func updateTopBarHidden() {
        hiddenNavigationBar()
        updateStatusHidden()
    }
}

extension WebViewViewController {
    
    func hiddenNavigationBar() {
        isNaviHidden = !isNaviHidden
        naviView.isHidden = isNaviHidden
        
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
    
    func updateNavigationBarAlpha() {
        isNaviAlpha = !isNaviAlpha
        naviView.alpha = isNaviAlpha ? 0.5 : 1.0
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
    
    func updateNavigationBarBackgroundColor() {
        naviView.backgroundColor = .orange
        
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
    
    func updateNavigationBarTintColor() {
        naviView.tineColor = .red
//        self.navigationController?.navigationBar.tintColor = .red
//        self.navigationController?.navigationBar.titleTextAttributes = [.foregroundColor: UIColor.red]
    }
    
    func hiddenNaviButton(hiddenString: String) {
        naviView.hiddenBtn = hiddenString == "1" ? false : true
    }
    
}

extension WebViewViewController {
    
    func updateStatusBackgroundColor() {
//        UIApplication.statusBarBackgroundColor = .orange
        statusView.backgroundColor = .orange
    }
    
    func updateStatusStyle() {
        if self.style == .default {
            self.style = .lightContent
        } else {
            self.style = .default
        }
        setNeedsStatusBarAppearanceUpdate()
    }
    
    func updateStatusHidden() {
        isStatusHidden = !isStatusHidden
        setNeedsStatusBarAppearanceUpdate()
    }
    
    func updateStatusBarAlpha() {
        isStatusAlpha = !isStatusAlpha
        statusView.alpha = isStatusAlpha ? 0.5 : 1.0
//        var frame = webView.frame
//        if isStatusAlpha {
//            frame.origin.y = 0
//            frame.size.height = UIScreen.main.bounds.height
//        } else {
//            if isNaviAlpha || isNaviHidden {
//                frame.origin.y = UIDevice.current.statusBarHeight()
//                frame.size.height -= UIDevice.current.statusBarHeight()
//            } else {
//                frame.origin.y = UIDevice.current.statusBarHeight() + 44
//                frame.size.height -= UIDevice.current.statusBarHeight() + 44
//            }
//        }
//        UIView.animate(withDuration: 0.25) {
//            self.webView.frame = frame
//            self.webView.updateFrame(frame: frame)
//        }
    }
}

extension WebViewViewController {
    
    func hiddenBottomView() {
        isBottomHidden = !isBottomHidden
        bottomView.isHidden = isBottomHidden
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
    
    func updateBottomViewAlpha() {
        isBottomAlpha = !isBottomAlpha
        bottomView.alpha = isBottomAlpha ? 0.5 : 1.0
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
    
    func updateBottomViewBackgroundColor() {
        bottomView.backgroundColor = .orange
    }
    
    func hiddenBottomViewButton(hiddenString: String) {
        bottomView.hiddenBtn = hiddenString == "1" ? false : true
    }
    
}
