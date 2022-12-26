//
//  BottomBarEntites.swift
//  BFExplorer
//
//  Created by ui03 on 2022/12/23.
//

import Foundation

extension PlaocHandleModel {
    //低部栏是否可点击
    func bottomBarShow(param: Any) -> Bool {
        return !(controller?.bottomView.bottomHiddenState() ?? true)
    }
    //设置低部栏是否可点击
    func updateBottomBarShow(param: Any) -> String {
        guard let param = param as? Bool else { return "" }
        controller?.bottomView.hiddenBottomView(hidden: param)
        return ""
    }
    //低部栏透明度
    func bottomBarAlpha(param: Any) -> CGFloat {
        return controller?.bottomView.bottomViewAlpha() ?? 0
    }
    //设置低部栏透明度
    func updateBottomBarAlpha(param: Any) -> String {
        guard let param = param as? String, Float(param) != nil else { return "" }
        controller?.bottomView.updaterBottomViewAlpha(alpha: CGFloat(Float(param)!))
        return ""
    }
    //底部栏高度
    func bottomBarHeight(param: Any) -> CGFloat {
        return controller?.bottomView.bottomViewHeight() ?? 0
    }
    //设置底部栏高度
    func updateBottomBarHeight(param: Any) -> String {
        guard let param = param as? String, Float(param) != nil else { return "" }
        controller?.updateBottomViewHeight(height: CGFloat(Float(param)!))
        return ""
    }
    //低部栏按钮
    func bottomBarActions(param: Any) -> String {
        return controller?.bottomView.bottomActions() ?? ""
    }
    //设置低部栏按钮
    func updateBottomBarActions(param: Any) -> String {
        guard let param = param as? String else { return "" }
        controller?.bottomView.fetchBottomButtons(content: param)
        return ""
    }
    //低部栏背景色
    func bottomBarBackgroundColor(param: Any) -> String {
        return controller?.bottomView.bottomBarBackgroundColor() ?? "#ffffffff"
    }
    //设置低部栏背景色
    func updateBottomBarBackgroundColor(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        controller?.bottomView.updateBottomViewBackgroundColor(colorString: param)
        return true
    }
    //低部栏前景色
    func bottomBarForegroundColor(param: Any) -> String {
        return controller?.bottomView.bottomBarForegroundColor() ?? "#ffffffff"
    }
    //低部栏前景色
    func updatebottomBarForegroundColor(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        controller?.bottomView.updateBottomViewforegroundColor(colorString: param)
        return true
    }
}
