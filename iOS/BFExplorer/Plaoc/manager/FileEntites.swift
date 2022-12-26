//
//  FileEntites.swift
//  BFExplorer
//
//  Created by ui03 on 2022/12/23.
//

import UIKit
import SwiftyJSON

extension PlaocHandleModel {

    // 获取dweb app home目录路径
    func getDwebAppPath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath + "/system-app/\(fileName)/home"
    }
    
    // 获取指定文件系统目录下的内容
    func executiveFileSystemLs(param: Any) -> String {
        guard let param = param as? String else { return "" }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + data["path"].stringValue) else {
            return ""
        }
        
        do {
            let urls = try FileSystemManager.readdir(at: url)
            
            if urls.count > 0 {
                let str = ChangeTools.arrayValueString(urls)
                
                return str ?? ""
            } else {
                return "[]"
            }
        } catch {
            return ""
        }
    }
    
    // 在指定文件系统目录下创建目录
    func executiveFileSystemMkdir(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
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
    func executiveFileSystemRm(param: Any) -> Bool {
        guard let param = param as? String else { return false }
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
    func executiveFileSystemRead(param: Any) -> String {
        guard let param = param as? String else { return "" }
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
    func executiveFileSystemWrite(param: Any) -> Bool {
        guard let param = param as? String else { return false }
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
    func executiveFileSystemStat(param: Any) -> String {
        guard let param = param as? String else { return "" }
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
            
            return str ?? ""
        } catch {
            return ""
        }
    }
    
    // 获取指定文件系统目录信息
    func executiveFileSystemList(param: Any) -> String {
        return ""
    }
}
