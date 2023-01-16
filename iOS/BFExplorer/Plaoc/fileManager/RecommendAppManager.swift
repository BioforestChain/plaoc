//
//  BatchRecommendManager.swift
//  BFS
//
//  Created by ui03 on 2022/9/6.
//

import UIKit

class RecommendAppManager: InnerAppManager {

    override var filePath: String { Bundle.main.bundlePath + "/app/recommend-app" }
    
    override var appInstalledPath: String {
        return documentdir + "/recommend-app"
    }
    
    override func iconImagePath(appId: String) -> String {
        return filePath + "/\(appId)/sys/"
    }
    
}
