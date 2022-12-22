//
//  URLGenerator.swift
//  Browser
//
//    21.    
//

import Foundation

class URLGenerator {
    private let urlValidator: URLValidator
    
    init(urlValidator: URLValidator = .init()) {
        self.urlValidator = urlValidator
    }
    
    func getURL(for text: String) -> URL? {
        let text = text.lowercased()
        guard urlValidator.isValidURL(text) else {
            return getGoogleSearchURL(for: text)
        }
        
        guard text.hasPrefix("http://") || text.hasPrefix("https://") else {
            return URL(string: "http://\(text)")
        }
        
        return URL(string: text)
    }
}

private extension URLGenerator {
    func getGoogleSearchURL(for text: String) -> URL? {
        guard let encodedSearchString = text.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed) else {
            return nil
        }
        let queryString = "https://cn.bing.com/search?q=\(encodedSearchString)"
        return URL(string: queryString)
    }
}
