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
    
    func pathPrefixReplace(_ path: String) -> String {
        return path.regexReplacePattern(pattern: "^[.]+", replaceString: "")
    }
    
    // 获取指定文件系统目录下的内容
    func executiveFileSystemLs(param: Any) -> String {
        guard let param = param as? String else { return "" }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return ""
        }
        
        do {
            let path = pathPrefixReplace(data["path"].stringValue)
            let urls = try FileSystemManager.readdir(at: url)
            
            if urls.count > 0 {
                
                let urlPaths = urls.map { $0.path }
                let str = ChangeTools.arrayValueString(urlPaths)
                
                return str ?? ""
            } else {
                return "[]"
            }
        } catch {
            print("ls error: \(error)")
            return "\(error)"
        }
    }
    
    // 在指定文件系统目录下创建目录
    func executiveFileSystemMkdir(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return false
        }
        
        do {
            try FileSystemManager.mkdir(at: URL(fileURLWithPath: url.path), recursive: data["option"]["recursive"].boolValue)
            
            return true
        } catch {
            print("mkdir error: \(error)")
            return false
        }
    }
    
    // 删除指定文件系统某个目录或文件
    func executiveFileSystemRm(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return false
        }
        
        do {
            // 获取文件类型
            let fileAttr = try FileSystemManager.stat(at: URL(fileURLWithPath: url.path))
            let fileType = FileSystemManager.getType(from: fileAttr)
            
            print("fileType: \(fileType)")
            
            if fileType == "file" {
                try FileSystemManager.deleteFile(at: URL(fileURLWithPath: url.path))
            } else {
                var recursive = true
                
                if data["option"]["deepDelete"].exists() {
                    recursive = data["option"]["deepDelete"].boolValue
                }
                
                try FileSystemManager.rmdir(at: URL(fileURLWithPath: url.path), recursive: recursive)
            }
            
            return true
        } catch {
            print("rm error: \(error)")
            return false
        }
    }
    
    // 读取指定文件系统内容
    func executiveFileSystemRead(param: Any) -> String {
        guard let param = param as? String else { return "" }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return ""
        }
        
        do {
            let result = try FileSystemManager.readFile(at: URL(fileURLWithPath: url.path), with: true)
            
            return result
        } catch {
            print("read error: \(error)")
            return "\(error)"
        }
    }
    
    // 在指定文件系统下写入内容
    func executiveFileSystemWrite(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return false
        }
        
        do {
            if data["option"]["append"].boolValue {
                try FileSystemManager.appendFile(at: URL(fileURLWithPath: url.path), with: data["content"].stringValue, recursive: data["option"]["autoCreate"].boolValue, with: true)
            } else {
                try FileSystemManager.writeFile(at: URL(fileURLWithPath: url.path), with: data["content"].stringValue, recursive: data["option"]["autoCreate"].boolValue, encoding: true)
            }
            
            return true
        } catch {
            print("write error: \(error)")
            return false
        }
    }
    
    // 获取指定文件系统目录或文件详细信息
    func executiveFileSystemStat(param: Any) -> String {
        guard let param = param as? String else { return "" }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return ""
        }
        
        do {
            // 获取文件类型
            let fileAttr = try FileSystemManager.stat(at: URL(fileURLWithPath: url.path))
            let fileType = FileSystemManager.getType(from: fileAttr)
            
            var dict: [String:Any] = [:]
            
            dict["type"] = fileType
            dict["size"] = fileAttr[.size]
            dict["uri"] = pathPrefixReplace(data["path"].stringValue)
            dict["mtime"] = (fileAttr[.modificationDate] as! NSDate).description
            dict["ctime"] = (fileAttr[.creationDate] as! NSDate).description

            let str = ChangeTools.dicValueString(dict)
            
            return str ?? ""
        } catch {
            print("stat error: \(error)")
            return "\(error)"
        }
    }
    
    // 获取指定文件系统目录信息
    func executiveFileSystemList(param: Any) -> String {
        guard let param = param as? String else { return "" }
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return ""
        }
        
        do {
            let result = StreamFileManager().list(fileName: fileName, filePath: url.path)
            let jsonEncoder = JSONEncoder()
            let jsonData = try jsonEncoder.encode(result)
            let jsonString = String(data: jsonData, encoding: .utf8)
            
            return jsonString ?? ""
        } catch {
            print("list error: \(error)")
            return "\(error)"
        }
    }
    
    // 重命名文件
    func executiveFileSystemRename(param: Any) -> Bool {
        guard let param = param as? String else {
            return false
        }
        
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return false
        }
        
        do {
            try FileSystemManager.rename(at: URL(fileURLWithPath: url.path), to: URL(fileURLWithPath: homePath + pathPrefixReplace(data["newPath"].stringValue)))
            
            return true
        } catch {
            print("rename error: \(error)")
            return false
        }
    }
    
    // 以buffer的形式读取文件
    func executiveFileSystemReadBuffer(param: Any) -> [UInt8]? {
        guard let param = param as? String else {
            return nil
        }
        
        let data = JSON.init(parseJSON: param)
        let homePath = getDwebAppPath()
        
        guard let url = URL(string: homePath + pathPrefixReplace(data["path"].stringValue)) else {
            return nil
        }
        
        let result = StreamFileManager().readFileData(filePath: url.path)
        
        
        if result != nil {
            return [UInt8](result!)
        } else {
            return nil
        }
    }
}
