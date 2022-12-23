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
        NotificationCenter.default.post(name: NSNotification.Name.openDwebNotification, object: nil, userInfo: ["param":param])
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
}









// FileSystem
extension PlaocHandleModel {
    // 获取dweb app home目录路径
    private func getDwebAppPath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath + "/system-app/\(fileName)/home"
    }
    
    // 获取指定文件系统目录下的内容
    private func executiveFileSystemLs(param: String) -> String {
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + data["path"].stringValue) else {
            return ""
        }
        
        do {
            let urls = try FileSystemManager.readdir(at: url)
            
            if urls.count > 0 {
                let str = ChangeTools.arrayValueString(urls)
                
                return str!
            } else {
                return "[]"
            }
        } catch {
            return ""
        }
    }
    
    // 在指定文件系统目录下创建目录
    private func executiveFileSystemMkdir(param: String) -> Bool {
        print("executiveFileSystemMkdir")
        print(param)
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        print("homePath: \(homePath)")
        
        guard let url = URL(string: homePath + data["path"].stringValue) else {
            return false
        }
        
        do {
            try FileSystemManager.mkdir(at: url, recursive: data["option"]["recursive"].boolValue)
            
            return true
        } catch {
            return false
        }
    }
    
    // 删除指定文件系统某个目录或文件
    private func executiveFileSystemRm(param: String) -> Bool {
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + data["path"].stringValue) else {
            return false
        }
        
        do {
            // 获取文件类型
            let fileAttr = try FileSystemManager.stat(at: url)
            let fileType = FileSystemManager.getType(from: fileAttr)
            
            if fileType == "file" {
                try FileSystemManager.deleteFile(at: url)
            } else {
                try FileSystemManager.rmdir(at: url, recursive: data["option"]["recursive"].boolValue)
            }
            
            return true
        } catch {
            return false
        }
    }
    
    // 读取指定文件系统内容
    private func executiveFileSystemRead(param: String) -> String {
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + data["path"].stringValue) else {
            return ""
        }
        
        do {
            let result = try FileSystemManager.readFile(at: url)
            
            return result
        } catch {
            return ""
        }
    }
    
    // 在指定文件系统下写入内容
    private func executiveFileSystemWrite(param: String) -> Bool {
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + data["path"].stringValue) else {
            return false
        }
        
        do {
            if data["option"]["append"].boolValue {
                try FileSystemManager.appendFile(at: url, with: data["content"].stringValue, recursive: data["option"]["recursive"].boolValue)
            } else {
                try FileSystemManager.writeFile(at: url, with: data["content"].stringValue, recursive: data["option"]["recursive"].boolValue)
            }
            
            return true
        } catch {
            return false
        }
    }
    
    // 获取指定文件系统目录或文件详细信息
    private func executiveFileSystemStat(param: String) -> String {
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + data["path"].stringValue) else {
            return ""
        }
        
        do {
            // 获取文件类型
            let fileAttr = try FileSystemManager.stat(at: url)
            let fileType = FileSystemManager.getType(from: fileAttr)
            
            var dict: [String:Any] = [:]
            
            dict["type"] = fileType
            dict["size"] = fileAttr[.size]
            dict["uri"] = URL(string: data["path"].stringValue)
            dict["mtime"] = fileAttr[.modificationDate]
            dict["ctime"] = fileAttr[.creationDate]
            
            let str = ChangeTools.dicValueString(dict)
            
            return str!
        } catch {
            return ""
        }
    }
    
    // 获取指定文件系统目录信息
    private func executiveFileSystemList(param: String) -> String {
        return ""
    }
}
