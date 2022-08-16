//
//  NaviView.swift
//  DWebBrowser
//
//  Created by mac on 2022/6/16.
//

import UIKit
import SDWebImage

typealias ClickViewCallback = (String) -> Void

class NaviView: UIView {

    var titleString: String? {
        didSet {
            titleLabel.text = titleString
        }
    }
    
    var tineColor: String? {
        didSet {
            guard tineColor != nil else { return }
            titleLabel.textColor = UIColor(tineColor!)
            backButton.setTitleColor(UIColor(tineColor!), for: .normal)
        }
    }
    
    private var buttonList: [UIButton] = []
    
    var buttons: [ButtonModel]? {
        didSet {
            guard buttons != nil else { return }
            for button in buttonList {
                button.removeFromSuperview()
            }
            buttonList.removeAll()
            
            let space: CGFloat = 16
            let width: CGFloat = 44
            
            let originX = self.frame.width - CGFloat(buttons!.count) * (width + space)
            
            for i in stride(from: 0, to: buttons!.count, by: 1) {
                let model = buttons![i]
                let button = UIButton(type: .contactAdd)
                button.tag = i
                let imageName = model.iconModel?.source ?? ""
                if model.iconModel?.type == "AssetIcon" {
                    button.sd_setImage(with: URL(string: imageName), for: .normal)
                } else {
                    if imageName.hasSuffix("svg") {
                        button.setImage(UIImage.svgImageNamed(imageName, size: CGSize(width: width, height: width)), for: .normal)
                    } else {
                        button.setImage(UIImage(named: imageName), for: .normal)
                    }
                }
                button.isEnabled = !(model.disabled ?? false)
//                button.menu = menuAction()
                button.addTarget(self, action: #selector(clickAction(sender:)), for: .touchUpInside)
                if #available(iOS 14.0, *) {
                    button.showsMenuAsPrimaryAction = true
                } else {
                    // Fallback on earlier versions
                }
                button.showsTouchWhenHighlighted = true
                button.frame = CGRect(x: originX + CGFloat(i) * (width + space), y: 0, width: width, height: width)
                self.addSubview(button)
                buttonList.append(button)
            }
        }
    }
    
    var callback: ClickViewCallback?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.backgroundColor = .white
        self.addSubview(backButton)
        self.addSubview(titleLabel)
        
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    lazy var backButton: UIButton = {
        let button = UIButton(type: .system)
        button.frame = CGRect(x: 16, y: 0, width: 50, height: self.frame.height)
        button.setTitle("返回", for: .normal)
        button.contentHorizontalAlignment = .left
        button.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        button.addTarget(self, action: #selector(backAction), for: .touchUpInside)
        return button
    }()
    
    lazy var titleLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: (self.frame.width - 200) * 0.5, y: 0, width: 200, height: self.frame.height))
        label.font = UIFont.systemFont(ofSize: 18)
        label.textAlignment = .center
        return label
    }()
}

extension NaviView {
    
    @objc private func backAction() {
        let controller = currentViewController()
        controller.navigationController?.popViewController(animated: true)
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
        guard code.count > 0 else { return }
        callback?(code)
    }
}
