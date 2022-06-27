//
//  UIView_extension.swift
//  DWebBrowser
//
//  Created by mac on 2022/4/21.
//

import UIKit

extension UIView {
    
    //获取某个view所在的控制器
    public func currentViewController() -> UIViewController {
        var viewController: UIViewController? = nil
        var next = self.next
        while (next != nil) {
            if next!.isKind(of: UIViewController.self) {
                viewController = (next as! UIViewController)
                break
            }
            next = next!.next
        }
        return viewController!
    }
}
