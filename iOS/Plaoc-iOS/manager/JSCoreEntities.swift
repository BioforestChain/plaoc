//
//  JSCoreHandle.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/11/4.
//

import UIKit
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
//        print("executiveFileSystemLs")
//        print(param)
        let _ = JSON.init(parseJSON: param)
//        FileSystemManager.ls(at: <#T##URL#>, with: <#T##String#>, recursive: <#T##Bool#>)
        
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
