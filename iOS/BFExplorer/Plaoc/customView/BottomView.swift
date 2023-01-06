//
//  BottomView.swift
//  DWebBrowser
//
//  Created by mac on 2022/6/16.
//

import UIKit
import SwiftyJSON

class BottomView: UIView {

    private let image_width: CGFloat = 50
    var hiddenBtn: Bool = false {
        didSet {
            for button in buttonList {
                button.isHidden = hiddenBtn
            }
        }
    }
    
    var buttons: [BottomBarModel]? {
        didSet {
            guard buttons != nil else { return }
            for button in buttonList {
                button.removeFromSuperview()
            }
            buttonList.removeAll()
            
            for i in stride(from: 0, to: buttons!.count, by: 1) {
                let model = buttons![i]
                let button = UIButton(type: .custom)
                button.frame = CGRect(x: CGFloat(i) * self.frame.width / CGFloat(buttons!.count), y: 0, width: self.frame.width / CGFloat(buttons!.count), height: self.frame.height)
                button.tag = i
                let imageName = model.iconModel?.source ?? ""
                if model.iconModel?.type == "AssetIcon" {
                    if imageName.hasSuffix("svg") {
                        button.setImage(UIImage.svgImage(withURL: imageName, size: CGSize(width: image_width, height: image_width)), for: .normal)
                    } else {
                        button.sd_setImage(with: URL(string: imageName), for: .normal)
                    }
                    
                } else {
                    if imageName.hasSuffix("svg") {
                        let name = imageName.replacingOccurrences(of: ".svg", with: "")
                        if model.colors?.iconColor != nil {
                            button.setImage(UIImage.svgImageNamed(name, size: CGSize(width: image_width, height: image_width), tintColor: UIColor((model.colors?.iconColor)!)), for: .normal)
                        } else {
                            button.setImage(UIImage.svgImageNamed(name, size: CGSize(width: image_width, height: image_width)), for: .normal)
                        }
                    } else {
                        button.setImage(UIImage(named: imageName), for: .normal)
                    }
                }
                button.isEnabled = !(model.disabled ?? false)
                button.isSelected = model.selected ?? false
                button.setTitle(model.titleString, for: .normal)
                
                var color = model.colors?.textColor ?? ""
                if color.isEmpty {
                    color = "#000000FF"
                }
                button.setTitleColor(UIColor(color), for: .normal)
                button.imagePosition(style: .top, spacing: 8)
//                button.menu = menuAction()
                button.addTarget(self, action: #selector(clickAction(sender:)), for: .touchUpInside)
//                button.showsMenuAsPrimaryAction = true
                
                self.addSubview(button)
                buttonList.append(button)
            }
        }
    }
    
    private var buttonList: [UIButton] = []
    private(set) var bottomOverlay: Bool = false
    var callback: ClickViewCallback?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.backgroundColor = .white
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func menuAction() -> UIMenu {
        
        let destruct = UIAction(title: "Destruct", attributes: .destructive) { _ in }

        let items = UIMenu(title: "More", options: .displayInline, children: [
            UIAction(title: "mic", image: UIImage(systemName: "mic"), handler: { _ in }),
            UIAction(title: "camera", image: UIImage(systemName: "camera"), handler: { _ in }),
            UIAction(title: "video", image: UIImage(systemName: "video"), handler: { _ in })
        ])
        let subItems = [
            UIAction(title: "画像1", image: UIImage(named: "alt"), handler: { _ in }),
            UIAction(title: "画像2", image: UIImage(named: "alt"), handler: { _ in }),
            UIAction(title: "画像3", image: UIImage(named: "alt"), handler: { _ in }),        ]
        //ボタンタップですぐ表示設定
        let photo = UIMenu(title: "photo", children: subItems)
//        sender.showsMenuAsPrimaryAction = true
        //itemの追加
        return UIMenu(title: "菜单", children: [items,photo,destruct])
    }
    
    @objc private func clickAction(sender: UIButton) {
        guard buttons != nil, sender.tag < buttons!.count else { return }
        let model = buttons![sender.tag]
        let code = model.onClickCode ?? ""
        for i in stride(from: 0, to: buttonList.count, by: 1) {
            let button = buttonList[i]
            let model = buttons![i]
            var color = model.colors?.textColor ?? ""
            if color.isEmpty {
                color = "#000000FF"
            }
            button.setTitleColor(UIColor(color), for: .normal)
            
            let imageName = model.iconModel?.source ?? ""
            if model.iconModel?.type == "AssetIcon" {
                
            } else {
                if imageName.hasSuffix("svg") {
                    let name = imageName.replacingOccurrences(of: ".svg", with: "")
                    if model.colors?.iconColor != nil {
                        button.setImage(UIImage.svgImageNamed(name, size: CGSize(width: image_width, height: image_width), tintColor: UIColor((model.colors?.iconColor)!)), for: .normal)
                    }
                }
            }
        }
        
        if sender.isHighlighted {
            var color = model.colors?.textColorSelected ?? ""
            if color.isEmpty {
                color = "#000000FF"
            }
            sender.setTitleColor(UIColor(color), for: .normal)
            
            let imageName = model.iconModel?.source ?? ""
            if model.iconModel?.type == "AssetIcon" {
                
            } else {
                if imageName.hasSuffix("svg") {
                    let name = imageName.replacingOccurrences(of: ".svg", with: "")
                    if model.colors?.iconColorSelected != nil {
                        sender.setImage(UIImage.svgImageNamed(name, size: CGSize(width: image_width, height: image_width), tintColor: UIColor((model.colors?.iconColorSelected)!)), for: .normal)
                    }
                }
            }
            
        }
        guard code.count > 0 else { return }
        callback?(code)
    }
}

extension BottomView {
    //隐藏底部
    func hiddenBottomView(hidden: Bool) {
        self.isHidden = hidden
    }
    //返回底部是否隐藏
    func bottomHiddenState() -> Bool {
        return self.isHidden
    }
    //更新底部overlay
    func updateBottomViewOverlay(overlay: Bool) {
        self.bottomOverlay = overlay
    }
    //返回底部overlay
    func bottomViewOverlay() -> Bool {
        return bottomOverlay
    }
    //设置底部alpha
    func updaterBottomViewAlpha(alpha: CGFloat) {
        self.alpha = alpha
    }
    //返回底部alpha
    func bottomViewAlpha() -> CGFloat {
        return self.alpha
    }
    //更新底部背景色
    func updateBottomViewBackgroundColor(colorString: String) {
        self.backgroundColor = UIColor(colorString)
    }
    //返回底部背景颜色
    func bottomBarBackgroundColor() -> String {
        return self.backgroundColor?.hexString() ?? "#FFFFFFFF"
    }
    //更新底部颜色
    func updateBottomViewforegroundColor(colorString: String) {
        //TODO
    }
    //返回底部颜色
    func bottomBarForegroundColor() -> String {
        //TODO
        return "#FFFFFFFF"
    }
    //返回底部高度
    func bottomViewHeight() -> CGFloat {
        return self.frame.height
    }
    //隐藏底部按钮
    func hiddenBottomViewButton(hidden: Bool) {
        self.hiddenBtn = hidden
    }
    
    //获取底部按钮
    func fetchBottomButtons(content: String) {
        guard let array = ChangeTools.stringValueArray(content) else { return }
        let list = JSON(array)
        let buttons = list.arrayValue.map { BottomBarModel(dict: $0) }
        self.buttons = buttons
    }
    //返回底部按钮数组
    func bottomActions() -> String {
        guard let buttons = self.buttons else { return "" }
        var array: [[String:Any]] = []
        for button in buttons {
            array.append(button.buttonDict)
        }
        let actionString = ChangeTools.arrayValueString(array) ?? ""
        return actionString
    }
}

