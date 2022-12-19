//
//  BatchSystemManager.swift
//  BFS
//
//  Created by ui03 on 2022/9/6.
//

import UIKit

class BatchSystemManager: BatchReadManager {

    private var mateDict: [String:Any]?
    override func filePath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath + "/system-app"
    }
    
    override func saveUpdateInfoFilePath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath + "/system-app"
    }
    
    override func iconImagePath(fileName: String) -> String {
        return filePath() + "/\(fileName)/sys/"
    }
    
    func fetchEntryPath(fileName: String) -> String? {
        guard let dict = readBFSAMatedataContent(fileName: fileName) else { return nil }
        guard let maniDict = dict["manifest"] as? [String:Any] else { return nil }
        guard var entryPath = maniDict["bfsaEntry"] as? String else { return nil }
        entryPath = entryPath.regexReplacePattern(pattern: "^(./|/|../)")
        let path = filePath() + "/\(fileName)/" + entryPath
        return path
    }
    
    //读取matedata.json文件
    func readBFSAMatedataContent(fileName: String) -> [String:Any]? {
        let path = filePath() + "/\(fileName)/boot/bfsa-metadata.json"
        let manager = FileManager.default
        guard let data = manager.contents(atPath: path) else { return nil }
        guard let content = String(data: data, encoding: .utf8) else { return nil }
        mateDict = ChangeTools.stringValueDic(content)
        return mateDict
    }
    //获取版本号
    func readMetadataVersion(fileName: String) -> String? {
        if mateDict == nil {
            _ = readBFSAMatedataContent(fileName: fileName)
        }
        return mateDict?["version"] as? String
    }
    //获取appType
    func readAppType(fileName: String) -> String? {
        if mateDict == nil {
            _ = readBFSAMatedataContent(fileName: fileName)
        }
        return mateDict?["apptype"] as? String
    }
    //获取web类型的网页地址
    func readWebAppURLString(fileName: String) -> String? {
        if mateDict == nil {
            _ = readBFSAMatedataContent(fileName: fileName)
        }
        return mateDict?["url"] as? String
    }
    
    
}
