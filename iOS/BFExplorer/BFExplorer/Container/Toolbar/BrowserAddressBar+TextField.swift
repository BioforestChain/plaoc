//
//  BrowserAddressBar+TextField.swift
//  Browser
//
//    11.    
//

import UIKit

extension BrowserAddressBar.TextField {
    enum State {
        case editing
        case inactive
    }
}
protocol TextFieldReloadDelegate: AnyObject {

    func reloadButtonClicked()
}

extension BrowserAddressBar {
    class TextField: UITextField {
        private let paddingView = UIView()
        private let magnifyingGlassImageView = UIImageView()
        private let aAButton = UIButton(type: .system)
        private let reloadButton = UIButton(type: .system)
        var tfDelegate: TextFieldReloadDelegate?
        var activityState = State.inactive {
            didSet {
                switch activityState {
                case .editing:
                    placeholder = nil
                    leftView = paddingView
                    rightView = nil
                    selectAll(nil)
                    textColor = .black
                case .inactive:
                    showDefaultPlaceholder()
                    leftView = hasText ? aAButton : magnifyingGlassImageView
                    rightView = hasText ? reloadButton : nil    
                    textColor = .clear
                }
            }
        }
        
        override init(frame: CGRect) {
            super.init(frame: frame)
            setupView()
            reloadButton.addTarget(self, action: #selector(reloadWebview), for: .touchUpInside)
            
        }
        
        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }
        
        override func leftViewRect(forBounds bounds: CGRect) -> CGRect {
            let width: CGFloat
            switch activityState {
            case .editing:
                width = 5
            case .inactive:
                width = 30
            }
            let height = CGFloat(22)
            let y = (bounds.height - height) / 2
            return CGRect(x: 10, y: y, width: width, height: height)
        }
        
        override func rightViewRect(forBounds bounds: CGRect) -> CGRect {
            let width = CGFloat(35)
            let height = CGFloat(22)
            let y = (bounds.height - height) / 2
            return CGRect(x: bounds.width - width - 5, y: y, width: width, height: height)
        }
        
        override func clearButtonRect(forBounds bounds: CGRect) -> CGRect {
            var rect = super.clearButtonRect(forBounds: bounds)
            rect.origin.x -= 15
            rect.size.width = 30
            return rect
        }
        
        @objc func reloadWebview(){
            self.tfDelegate?.reloadButtonClicked()
        }
    }
}

// MARK: Helper methods
private extension BrowserAddressBar.TextField {
    func setupView() {
        layer.cornerRadius = 5
        backgroundColor = .white
        clearButtonMode = .whileEditing
        returnKeyType = .go
        leftViewMode = .always
        rightViewMode = .always
        autocorrectionType = .no
        autocapitalizationType = .none
        keyboardType = .webSearch
        enablesReturnKeyAutomatically = true
        clipsToBounds = true
        setupMagnifyingGlassImage()
        setupAaButton()
        setupReloadButton()
        activityState = .inactive
    }
    
    func setupMagnifyingGlassImage() {
        magnifyingGlassImageView.image = UIImage(systemName: "magnifyingglass")?.withRenderingMode(.alwaysTemplate)
        magnifyingGlassImageView.tintColor = .textFieldGray
        magnifyingGlassImageView.contentMode = .scaleAspectFit
    }
    
    func setupAaButton() {
        aAButton.setImage(UIImage(systemName: "textformat.size")?.withRenderingMode(.alwaysTemplate), for: .normal)
        aAButton.imageView?.contentMode = .scaleAspectFit
        aAButton.tintColor = .black
        aAButton.isHidden = true
    }
    
    func setupReloadButton() {
        reloadButton.setImage(UIImage(systemName: "arrow.clockwise")?.withRenderingMode(.alwaysTemplate), for: .normal)
        reloadButton.imageView?.contentMode = .scaleAspectFit
        reloadButton.tintColor = mainTintColor

    }
    
    func showDefaultPlaceholder() {
        let attributes: [NSAttributedString.Key: Any] = [.foregroundColor: UIColor.textFieldGray]
        attributedPlaceholder = NSAttributedString(string: "搜索或输入网址", attributes: attributes)
    }
    

}
