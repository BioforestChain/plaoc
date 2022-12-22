//
//  ClipboardManager.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/12/19.
//

import UIKit

class ClipboardManager: NSObject {

    enum ContentType {
        case string
        case url
        case image
        case color
    }
    
    enum ClipboardError: LocalizedError {
        case invalidURL, invalidImage, invalidColor, invalidString
        
        public var errorDescription: String? {
            switch self {
            case .invalidURL:
                return "Unable to form URL"
            case .invalidImage:
                return "Unable to encode Image"
            case .invalidColor:
                return "Unable to form Color"
            case .invalidString:
                return "Unable to form String"
            }
        }
    }
    
    static func write(content: Any, ofType type: ContentType) ->Result<Void, Error> {
        switch type {
        case .string:
            if content is String {
                UIPasteboard.general.string = content as? String
                return .success(())
            } else {
                return .failure(ClipboardError.invalidString)
            }
        case .url:
            if content is URL {
                UIPasteboard.general.url = content as? URL
                return .success(())
            } else {
                return .failure(ClipboardError.invalidURL)
            }
        case .image:
            if content is UIImage {
                UIPasteboard.general.image = content as? UIImage
                return .success(())
            } else {
                return .failure(ClipboardError.invalidImage)
            }
        case .color:
            if content is UIImage {
                UIPasteboard.general.color = content as? UIColor
                return .success(())
            } else {
                return .failure(ClipboardError.invalidColor)
            }
        }
    }
    
    static func read(type: ContentType) -> Any? {
        switch type {
        case .string:
            return UIPasteboard.general.string
        case .url:
            return UIPasteboard.general.url
        case .image:
            return UIPasteboard.general.image
        case .color:
            return UIPasteboard.general.color
        }
    }
}
