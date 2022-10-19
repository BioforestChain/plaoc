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
    
    //读取matedata.json文件
    func readBFSAMatedataContent(fileName: String) -> [String:Any]? {
        let path = filePath() + "/\(fileName)/boot/bfsa-matedata.json"
        let manager = FileManager.default
        guard let data = manager.contents(atPath: path) else { return nil }
        guard let content = String(data: data, encoding: .utf8) else { return nil }
        mateDict = ChangeTools.stringValueDic(content)
        return mateDict
    }
    
    func readMetadataVersion(fileName: String) -> String? {
        if mateDict == nil {
            _ = readBFSAMatedataContent(fileName: fileName)
        }
        return mateDict?["version"] as? String
    }
}
