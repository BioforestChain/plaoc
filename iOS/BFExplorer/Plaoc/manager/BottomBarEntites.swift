//
//  BottomBarEntites.swift
//  BFExplorer
//
//  Created by ui03 on 2022/12/23.
//

import Foundation

extension PlaocHandleModel {
    //低部栏是否可点击
    func bottomBarEnabled(param: String) -> Bool {
        return true
    }
    //设置低部栏是否可点击
    func updateBottomBarEnabled(param: String) -> String {
        return ""
    }
    //低部栏透明度
    func bottomBarAlpha(param: String) -> CGFloat {
        return 0.8
    }
    //设置低部栏透明度
    func updateBottomBarAlpha(param: String) -> String {
        return ""
    }
    //底部栏高度
    func bottomBarHeight(param: String) -> CGFloat {
        return 30
    }
    //设置底部栏高度
    func updateBottomBarHeight(param: String) -> String {
        return ""
    }
    //低部栏按钮
    func bottomBarActions(param: String) -> String {
        return ""
    }
    //设置低部栏按钮
    func updateBottomBarActions(param: String) -> String {
        return ""
    }
    //低部栏背景色
    func bottomBarBackgroundColor(param: String) -> String {
        return ""
    }
    //设置低部栏背景色
    func updateBottomBarBackgroundColor(param: String) -> String {
        return ""
    }
    //低部栏前景色
    func bottomBarForegroundColor(param: String) -> String {
        return ""
    }
    //低部栏前景色
    func updatebottomBarForegroundColor(param: String) -> String {
        return ""
    }
}
