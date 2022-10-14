//
//  BatchReadManager.swift
//  BFS
//
//  Created by ui03 on 2022/9/6.
//

import UIKit

class BatchReadManager: NSObject {

    var linkDict: [String: Any] = [:]
    
    
    func filePath() -> String {  return "" }
    
    func saveUpdateInfoFilePath() -> String { return "" }
    
    func iconImagePath(fileName: String) -> String { return "" }
    
    //读取system-app文件夹下面的文件夹
    func readAppSubFile() -> [String] {
        return subFilePathNames(atPath: filePath())
    }
    
    //读取文件夹下面的第一级文件夹
    private func subFilePathNames(atPath path: String) -> [String] {
        var fileList: [String] = []
        guard let filePaths = FileManager.default.subpaths(atPath: path) else { return fileList }
        for fileName in filePaths {
            var isDir: ObjCBool = true
            let fullPath = "\(path)/\(fileName)"
            if FileManager.default.fileExists(atPath: fullPath, isDirectory: &isDir) {
                if !isDir.boolValue {
                    
                } else {
                    //后续是不需要判断.的，因为这是临时添加的，后续从网络获取
                    if !fileName.contains("/"), !fileName.contains(".") {
                        fileList.append(fileName)
                    }
                }
            }
        }
        return fileList
    }
    
    //读取bfs-app-id 的link文件信息
    private func readBFSAppLinkContent(fileName: String) {
        let path = filePath() + "/\(fileName)/boot/link.json"
        let manager = FileManager.default
        guard let data = manager.contents(atPath: path) else { return }
        guard let content = String(data: data, encoding: .utf8) else { return }
        let tmpLinkDict = ChangeTools.stringValueDic(content)
        linkDict[fileName] = tmpLinkDict
    }
    
    //app名称
    func appName(fileName: String) -> String {
        var dict = linkDict[fileName] as? [String:Any]
        if dict == nil {
            readBFSAppLinkContent(fileName: fileName)
            dict = linkDict[fileName] as? [String:Any]
        }
        return dict?["name"] as? String ?? ""
    }
    //app图片
    func appIcon(fileName: String) -> UIImage? {
        
        var dict = linkDict[fileName] as? [String:Any]
        if dict == nil {
            readBFSAppLinkContent(fileName: fileName)
            dict = linkDict[fileName] as? [String:Any]
        }
        
        var imageName = dict?["icon"] as? String ?? ""
        if imageName.hasPrefix("file://") {
            imageName = imageName.replacingOccurrences(of: "file://", with: "")
        } 
        let imagePath = iconImagePath(fileName: fileName) + imageName
        var image = UIImage(contentsOfFile: imagePath)
        if imagePath.hasSuffix(".svg") {
            image = UIImage.svgImage(withContentsOfFile: imagePath, size: CGSize(width: 28, height: 28))
        }
        return image
    }
    //appID
    func appID(fileName: String) -> String {
        var dict = linkDict[fileName] as? [String:Any]
        if dict == nil {
            readBFSAppLinkContent(fileName: fileName)
            dict = linkDict[fileName] as? [String:Any]
        }
        return dict?["bfsAppId"] as? String ?? ""
    }
    
    //读取轮询更新缓存信息  取文件夹下面的很多文件
    func readCacheUpdateInfo(fileName: String) -> [String:Any]? {
        let filePath = saveUpdateInfoFilePath()
        let updatePath = filePath + "/\(fileName)/tmp/autoUpdate/"
        let manager = FileManager.default
        let subContents = try? manager.contentsOfDirectory(atPath: updatePath).sorted { $0 > $1 }
        guard let first = subContents?.first else { return nil }
        let path = updatePath + first
        guard let data = manager.contents(atPath: path) else { return nil }
        guard let cacheString = String(data: data, encoding: .utf8) else { return nil }
        return ChangeTools.stringValueDic(cacheString)
    }
    
    
    //写入轮询更新的机制信息
    func writeUpdateInfoToTmpFile(fileName: String, json: [String:Any]) {
        let filePath = saveUpdateInfoFilePath()
        var updatePath = filePath + "/\(fileName)/tmp/autoUpdate/"
        if !FileManager.default.fileExists(atPath: updatePath) {
            try? FileManager.default.createDirectory(atPath: updatePath, withIntermediateDirectories: true)
        }
        var currentTime = Date().dateToString(identifier: "UTC")
        currentTime = currentTime.replacingOccurrences(of: ":", with: "")
        currentTime = currentTime.replacingOccurrences(of: " ", with: "")
        updatePath = updatePath + "\(currentTime).json"
        let jsonString = ChangeTools.dicValueString(json) ?? ""
        guard jsonString.count > 0 else { return }
        if !FileManager.default.fileExists(atPath: updatePath) {
            FileManager.default.createFile(atPath: updatePath, contents: nil)
        }
//        try? jsonString.write(toFile: updatePath, atomically: true, encoding: .utf8)
        
        do {
            try jsonString.write(toFile: updatePath, atomically: true, encoding: .utf8)
        } catch {
            print(error.localizedDescription)
        }
    }
    
    func isNewUpdateInfo(fileName: String) -> Bool {
        let filePath = saveUpdateInfoFilePath()
        let updatePath = filePath + "/\(fileName)/tmp/autoUpdate/"
        let subContents = try? FileManager.default.contentsOfDirectory(atPath: updatePath).sorted { $0 > $1 }
        guard subContents != nil, subContents!.count > 1 else { return true }
        
        let first = subContents![0]
        let second = subContents![1]
        
        let firstPath = updatePath + first
        let secondPath = updatePath + second
        
        let firstVersion = versionInfo(name: firstPath)
        let secondVersion = versionInfo(name: secondPath)
        let result = firstVersion.versionCompare(oldVersion: secondVersion)
        if result == .orderedAscending {
            return true
        }
        return false
        
//        if firstVersion > secondVersion {
//            return true
//        }
//        return false
    }
                                  
    private func versionInfo(name: String) -> String {
        if let data = FileManager.default.contents(atPath: name) {
            if let cacheString = String(data: data, encoding: .utf8) {
                let oldDict = ChangeTools.stringValueDic(cacheString)
                let version = oldDict?["version"] as? String ?? ""
                return version
//                let versionDouble = Double(version) ?? 0
//                return versionDouble
            }
        }
        return ""
    }
    
    func readAutoUpdateURLInfo(fileName: String) -> String? {
        var dict = linkDict[fileName] as? [String:Any]
        if dict == nil {
            readBFSAppLinkContent(fileName: fileName)
            dict = linkDict[fileName] as? [String:Any]
        }
        guard let updateDict = dict?["autoUpdate"] as? [String:Any] else { return nil }
        return updateDict["url"] as? String
    }
    
    func readAutoUpdateMaxAge(fileName: String) -> Int? {
        var dict = linkDict[fileName] as? [String:Any]
        if dict == nil {
            readBFSAppLinkContent(fileName: fileName)
            dict = linkDict[fileName] as? [String:Any]
        }
        guard let updateDict = dict?["autoUpdate"] as? [String:Any] else { return nil }
        return updateDict["maxAge"] as? Int
    }
}
