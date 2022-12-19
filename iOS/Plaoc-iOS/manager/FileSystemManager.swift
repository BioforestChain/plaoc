//
//  FileSystemManager.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/12/19.
//

import UIKit

class FileSystemManager: NSObject {

    public enum FilesystemError: LocalizedError {
        case noParentFolder, noSave, failEncode, noAppend, notEmpty
        
        public var errorDescription: String? {
            switch self {
            case .noParentFolder:
                return "Parent folder doesn't exist"
            case .noSave:
                return "Unable to save file"
            case .failEncode:
                return "Unable to encode data to utf-8"
            case .noAppend:
                return "Unable to append file"
            case .notEmpty:
                return "Folder is not empty"
            }
        }
    }
    
    static func readFile(at fileUrl: URL, with encoding: Bool = false) throws -> String {
        if encoding {
            let data = try String(contentsOf: fileUrl, encoding: .utf8)
            return data
        } else {
            let data = try Data(contentsOf: fileUrl)
            return data.base64EncodedString()
        }
    }
    
    static func writeFile(at fileUrl: URL, with data: String, recursive: Bool, encoding: Bool = false) throws -> String {
        
        if !FileManager.default.fileExists(atPath: fileUrl.deletingLastPathComponent().path) {
            if recursive {
                try FileManager.default.createDirectory(at: fileUrl.deletingLastPathComponent(), withIntermediateDirectories: recursive)
            } else {
                throw FilesystemError.noParentFolder
            }
        }
        if encoding {
            try data.write(to: fileUrl, atomically: false, encoding: .utf8)
        } else {
            if let base64Data = Data(base64Encoded: data) {
                try base64Data.write(to: fileUrl)
            } else {
                throw FilesystemError.noSave
            }
        }
        return fileUrl.absoluteString
    }
    
    static func appendFile(at fileUrl: URL, with data: String, recursive: Bool, with encoding: Bool = false) throws {
        if FileManager.default.fileExists(atPath: fileUrl.path) {
            let fileHandle = try FileHandle.init(forWritingTo: fileUrl)
            var writeData: Data?
            if encoding {
//                guard let userData = data
            }
        } else {
            _ = try writeFile(at: fileUrl, with: data, recursive: recursive, encoding: encoding)
        }
    }
}
