//
//  JSCoreHandle.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/11/4.
//

import Foundation
import JavaScriptCore
import SwiftyJSON

@objc protocol PlaocJSExport: JSExport {
    
    func callJavaScript(functionName: String, param: String) -> Any
    
}


@objc class PlaocHandleModel: NSObject, PlaocJSExport {
    
    var controller: UIViewController?
    var jsContext: JSContext?
    var fileName: String = ""
    
    func callJavaScript(functionName: String, param: String) -> Any {
        print(functionName)
        switch functionName {
        case "OpenDWebView":
            return executiveOpenDWebView(param: param)
        case "OpenQrScanner":
            return executiveOpenQrScanner(param: param)
        case "BarcodeScanner":
            return executiveOpenBarcodeScanner(param: param)
        case "InitMetaData":
            return executiveInitMetaData(param: param)
        case "DenoRuntime":
            return executiveDenoRuntime(param: param)
        case "GetBfsAppId":
            return executiveGetBfsAppId(param: param)
        case "EvalJsRuntime":
            return executiveEvalJsRuntime(param: param)
        case "GetDeviceInfo":
            return executiveGetDeviceInfo(param: param)
        case "SendNotification":
            return executiveSendNotification(param: param)
        case "GetNotification":
            return executiveGetNotification(param: param)
        case "ApplyPermissions":
            return executiveApplyPermissions(param: param)
        case "isDenoRuntime":
            return executiveIsDenoRuntime(param: param)
        case "FileSystemLs":
            return executiveFileSystemLs(param: param)
        case "FileSystemMkdir":
            return executiveFileSystemMkdir(param: param)
        case "FileSystemRm":
            return executiveFileSystemRm(param: param)
        case "FileSystemRead":
            return executiveFileSystemRead(param: param)
        case "FileSystemWrite":
            return executiveFileSystemWrite(param: param)
        case "FileSystemStat":
            return executiveFileSystemStat(param: param)
        case "FileSystemList":
            return executiveFileSystemList(param: param)
        case "SetStatusBarColor":
            return updateStatusBarColor(param: param)
        case "GetStatusBarColor":
            return statusBarColor(param: param)
        case "GetStatusBarIsDark":
            return statusBarIsDark(param: param)
        case "GetStatusBarVisible":
            return statusBarVisible(param: param)
        case "SetStatusBarVisible":
            return updateStatusBarVisible(param: param)
        case "GetStatusBarOverlay":
            return statusBarOverlay(param: param)
        case "SetStatusBarOverlay":
            return updateStatusBarOverlay(param: param)
        case "GetKeyBoardSafeArea":
            return keyboardSafeArea(param: param)
        case "GetKeyBoardHeight":
            return keyboardHeight(param: param)
        case "GetKeyBoardOverlay":
            return keyboardOverlay(param: param)
        case "SetKeyBoardOverlay":
            return updateKeyboardOverlay(param: param)
        case "ShowKeyBoard":
            return showKeyboard(param: param)
        case "HideKeyBoard":
            return hideKeyBoard(param: param)
        case "TopBarNavigationBack":
            return topBarNavigationBack(param: param)
        case "GetTopBarShow":
            return topbarShow(param: param)
        case "SetTopBarShow":
            return updateTopBarShow(param: param)
        case "GetTopBarOverlay":
            return topBarOverlay(param: param)
        case "SetTopBarOverlay":
            return updateTopBarOverlay(param: param)
        case "GetTopBarAlpha":
            return topBarAlpha(param: param)
        case "SetTopBarAlpha":
            return updateTopBarAlpha(param: param)
        case "GetTopBarTitle":
            return topBarTitle(param: param)
        case "SetTopBarTitle":
            return updateTopBarTitle(param: param)
        case "HasTopBarTitle":
            return isTopBarTitle(param: param)
        case "GetTopBarHeight":
            return topBarHeight(param: param)
        case "GetTopBarActions":
            return topBarActions(param: param)
        case "SetTopBarActions":
            return updateTopBarActions(param: param)
        case "GetTopBarBackgroundColor":
            return topBarBackgroundColor(param: param)
        case "SetTopBarBackgroundColor":
            return updateTopBarBackgroundColor(param: param)
        case "GetTopBarForegroundColor":
            return topBarForegroundColor(param: param)
        case "SetTopBarForegroundColor":
            return updateTopBarForegroundColor(param: param)
        case "GetBottomBarEnabled":
            return bottomBarEnabled(param: param)
        case "SetBottomBarEnabled":
            return updateBottomBarEnabled(param: param)
        case "GetBottomBarAlpha":
            return bottomBarAlpha(param: param)
        case "SetBottomBarAlpha":
            return updateBottomBarAlpha(param: param)
        case "GetBottomBarHeight":
            return bottomBarHeight(param: param)
        case "SetBottomBarHeight":
            return updateBottomBarHeight(param: param)
        case "GetBottomBarActions":
            return bottomBarActions(param: param)
        case "SetBottomBarActions":
            return updateBottomBarActions(param: param)
        case "GetBottomBarBackgroundColor":
            return bottomBarBackgroundColor(param: param)
        case "SetBottomBarBackgroundColor":
            return updateBottomBarBackgroundColor(param: param)
        case "GetBottomBarForegroundColor":
            return bottomBarForegroundColor(param: param)
        case "SetBottomBarForegroundColor":
            return updatebottomBarForegroundColor(param: param)
        case "OpenDialogAlert":
            return OpenDialogAlert(param: param)
        case "OpenDialogPrompt":
            return OpenDialogPrompt(param: param)
        case "OpenDialogConfirm":
            return OpenDialogConfirm(param: param)
        case "OpenDialogWarning":
            return OpenDialogWarning(param: param)
        default:
            return ""
        }
    }
}


extension PlaocHandleModel {
    //打开DWebView
    private func executiveOpenDWebView(param: String) -> Bool {
        NotificationCenter.default.post(name: NSNotification.Name.openDwebNotification, object: nil, userInfo: ["param":"iosqmkkx:/\(param)"])
        return true
    }
    //二维码
    private func executiveOpenQrScanner(param: String) -> String {
        let scanVC = ScanPhotoViewController()
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //条形码
    private func executiveOpenBarcodeScanner(param: String) -> String {
        let scanVC = ScanPhotoViewController()
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //初始化app数据
    private func executiveInitMetaData(param: String) -> String {
        NetworkMap.shared.metaData(metadata: param, fileName: fileName)
        return ""
    }
    //初始化运行时
    private func executiveDenoRuntime(param: String) -> String {
        return ""
    }
    //获取appID
    private func executiveGetBfsAppId(param: String) -> String {
        return ""
    }
    //传递给前端消息
    private func executiveEvalJsRuntime(param: String) -> String {
//        return jsContext?.evaluateScript(param)
        return ""
    }
    //获取设备信息
    private func executiveGetDeviceInfo(param: String) -> String {
        return ""
    }
    //发送消息  调用系统通知
    private func executiveSendNotification(param: String) -> String {
        return ""
    }
    //获取推送消息列表
    private func executiveGetNotification(param: String) -> String {
        return ""
    }
    //申请权限
    private func executiveApplyPermissions(param: String) -> String {
        let permission = PermissionManager()
        permission.startPermissionAuthenticate(type: .bluetooth) { result in
            
        }
        return ""
    }
    //
    private func executiveIsDenoRuntime(param: String) -> String {
        return "true"
    }
    
    // 获取指定文件系统目录下的内容
    private func executiveFileSystemLs(param: String) -> String {
        
        return ""
    }
    
    // 在指定文件系统目录下创建目录
    private func executiveFileSystemMkdir(param: String) -> String {
        print("executiveFileSystemMkdir")
        print(param)
        let data = JSON.init(parseJSON: param)
        if let url = URL(string: data["path"].stringValue) {
            try? FileSystemManager.mkdir(at: url, recursive: data["option"]["recursive"].boolValue)
        }
        
        return ""
    }
    
    // 删除指定文件系统某个目录或文件
    private func executiveFileSystemRm(param: String) -> String {
        return ""
    }
    
    // 读取指定文件系统内容
    private func executiveFileSystemRead(param: String) -> String {
        return ""
    }
    
    // 在指定文件系统下写入内容
    private func executiveFileSystemWrite(param: String) -> String {
        return ""
    }
    
    // 获取指定文件系统目录或文件详细信息
    private func executiveFileSystemStat(param: String) -> String {
        return ""
    }
    
    // 获取指定文件系统目录或文件构造信息
    private func executiveFileSystemList(param: String) -> String {
        return ""
    }
}

extension PlaocHandleModel {
    //设置状态栏颜色
    private func updateStatusBarColor(param: String) -> String {
        return ""
    }
    //获取状态栏颜色
    private func statusBarColor(param: String) -> String {
        return ""
    }
    //状态栏是否是暗黑模式
    private func statusBarIsDark(param: String) -> Bool {
        return false
    }
    //状态栏是否可见
    private func statusBarVisible(param: String) -> Bool {
        return true
    }
    //设置状态栏是否可见
    private func updateStatusBarVisible(param: String) -> String {
        return ""
    }
    //状态栏是否overlay
    private func statusBarOverlay(param: String) -> Bool {
        return true
    }
    //设置状态栏overlay
    private func updateStatusBarOverlay(param: String) -> String {
        return ""
    }
}

extension PlaocHandleModel {
    //键盘安全区域
    private func keyboardSafeArea(param: String) -> CGRect {
        return CGRect.zero
    }
    //键盘高度
    private func keyboardHeight(param: String) -> CGFloat {
        return 0
    }
    //显示键盘
    private func showKeyboard(param: String) -> Bool {
        return true
    }
    //隐藏键盘
    private func hideKeyBoard(param: String) -> Bool {
        return true
    }
    //键盘是否overlay
    private func keyboardOverlay(param: String) -> Bool {
        return true
    }
    //设置键盘overlay
    private func updateKeyboardOverlay(param: String) -> String {
        return ""
    }
}

extension PlaocHandleModel {
    //顶部栏返回
    private func topBarNavigationBack(param: String) -> String {
        return ""
    }
    //顶部栏是否显示
    private func topbarShow(param: String) -> Bool {
        return true
    }
    //设置顶部栏是否显示
    private func updateTopBarShow(param: String) -> String {
        return ""
    }
    //状态栏透明度
    private func topBarAlpha(param: String) -> CGFloat {
        return 0.8
    }
    //设置状态栏透明度
    private func updateTopBarAlpha(param: String) -> String {
        return ""
    }
    //顶部栏是否overlay
    private func topBarOverlay(param: String) -> Bool {
        return true
    }
    //设置顶部栏overlay
    private func updateTopBarOverlay(param: String) -> String {
        return ""
    }
    //顶部栏标题
    private func topBarTitle(param: String) -> String {
        return ""
    }
    //设置顶部栏标题
    private func updateTopBarTitle(param: String) -> String {
        return ""
    }
    //顶部栏是否标题
    private func isTopBarTitle(param: String) -> Bool {
        return true
    }
    //设置顶部栏标题
    private func topBarHeight(param: String) -> CGFloat {
        return 30
    }
    //顶部栏按钮
    private func topBarActions(param: String) -> String {
        return ""
    }
    //设置顶部栏按钮
    private func updateTopBarActions(param: String) -> String {
        return ""
    }
    //顶部栏背景色
    private func topBarBackgroundColor(param: String) -> String {
        return ""
    }
    //设置顶部栏背景色
    private func updateTopBarBackgroundColor(param: String) -> String {
        return ""
    }
    //顶部栏前景色
    private func topBarForegroundColor(param: String) -> String {
        return ""
    }
    //设置顶部栏前景色
    private func updateTopBarForegroundColor(param: String) -> String {
        return ""
    }
}

extension PlaocHandleModel {
    //低部栏是否可点击
    private func bottomBarEnabled(param: String) -> Bool {
        return true
    }
    //设置低部栏是否可点击
    private func updateBottomBarEnabled(param: String) -> String {
        return ""
    }
    //低部栏透明度
    private func bottomBarAlpha(param: String) -> CGFloat {
        return 0.8
    }
    //设置低部栏透明度
    private func updateBottomBarAlpha(param: String) -> String {
        return ""
    }
    //底部栏高度
    private func bottomBarHeight(param: String) -> CGFloat {
        return 30
    }
    //设置底部栏高度
    private func updateBottomBarHeight(param: String) -> String {
        return ""
    }
    //低部栏按钮
    private func bottomBarActions(param: String) -> String {
        return ""
    }
    //设置低部栏按钮
    private func updateBottomBarActions(param: String) -> String {
        return ""
    }
    //低部栏背景色
    private func bottomBarBackgroundColor(param: String) -> String {
        return ""
    }
    //设置低部栏背景色
    private func updateBottomBarBackgroundColor(param: String) -> String {
        return ""
    }
    //低部栏前景色
    private func bottomBarForegroundColor(param: String) -> String {
        return ""
    }
    //低部栏前景色
    private func updatebottomBarForegroundColor(param: String) -> String {
        return ""
    }
}

extension PlaocHandleModel {
    //alert弹框
    private func OpenDialogAlert(param: String) -> String {
        return ""
    }
    //prompt弹框
    private func OpenDialogPrompt(param: String) -> String {
        return ""
    }
    //firm弹框
    private func OpenDialogConfirm(param: String) -> String {
        return ""
    }
    //warning弹框
    private func OpenDialogWarning(param: String) -> String {
        return ""
    }
}

