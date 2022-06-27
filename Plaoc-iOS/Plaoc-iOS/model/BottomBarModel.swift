//
//  BottomBarModel.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/23.
//

import Foundation
import SwiftyJSON

struct BottomBarModel {
    
    var iconModel: IconModel?
    var onClickCode: String?
    var titleString: String?
    var disabled: Bool?
    var selected: Bool?
    var selectable: Bool?
    var colors: ColorModel?
    
    init(dict: JSON) {
        iconModel = IconModel(dict: dict["icon"])
        onClickCode = dict["onClickCode"].stringValue
        titleString = dict["titleString"].stringValue
        disabled = dict["disabled"].boolValue
        selected = dict["selected"].boolValue
        selectable = dict["selectable"].boolValue
        colors = ColorModel(dict: dict["colors"])
    }
    
    var buttonDict: [String:Any] {
        return ["icon":iconModel?.iconDict,
                "colors":colors?.colorDict,
                "onClickCode":onClickCode,
                "titleString":titleString,
                "disabled":disabled,
                "selected":selected,
                "selectable":selectable]
    }
}

struct ColorModel {
    
    var indicatorColor: Int?
    var iconColor: Int?
    var iconColorSelected: Int?
    var textColor: Int?
    var textColorSelected: Int?
    
    init(dict: JSON) {
        
        indicatorColor = dict["indicatorColor"].intValue
        iconColor = dict["iconColor"].intValue
        iconColorSelected = dict["iconColorSelected"].intValue
        textColor = dict["textColor"].intValue
        textColorSelected = dict["textColorSelected"].intValue
    }
    
    var colorDict: [String:Any] {
        return ["indicatorColor":indicatorColor,
                "iconColor":iconColor,
                "iconColorSelected":iconColorSelected,
                "textColor":textColor,
                "textColorSelected":textColorSelected]
    }
}
