//
//  BatchRecommendManager.swift
//  BFS
//
//  Created by ui03 on 2022/9/6.
//

import UIKit

class BatchRecommendManager: BatchReadManager {

    override func filePath() -> String {
        return Bundle.main.bundlePath + "/recommend-app"
    }
    
    override func saveUpdateInfoFilePath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath + "/recommend-app"
    }
    
    override func iconImagePath(fileName: String) -> String {
        return filePath() + "/\(fileName)/sys/"
    }
}
