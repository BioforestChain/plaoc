//
//  String_extension.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/7/14.
//

import Foundation

extension String {
    //16进制转data
    public func hexData() -> Data? {
        
        var data = Data(capacity: count / 2)
        let regex = try? NSRegularExpression(pattern: "[0-9a-f]{1,2}", options: .caseInsensitive)
        regex?.enumerateMatches(in: self, range: NSMakeRange(0, utf16.count), using: { result, flags, pointer in
            guard result != nil else { return }
            let byteString = (self as NSString).substring(with: result!.range)
            if var num = UInt8(byteString, radix: 16) {
                data.append(&num, count: 1)
            }
        })
        guard data.count > 0 else { return nil }
        return data
    }
    
    public func utf8Data() -> Data? {
        return self.data(using: .utf8)
    }
}
