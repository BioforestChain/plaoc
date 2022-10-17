//
//  PhotoManager.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/7/22.
//

import UIKit

let photoManager = PhotoManager()

class PhotoManager: NSObject {

    private var isPermissioned: Bool = false
    private var isScan: Bool = false
    
    private func permissioned() {
        permissionManager.startPermissionAuthenticate(type: .photo, isSet: true) { [weak self] result in
            guard let strongSelf = self else { return }
            strongSelf.isPermissioned = result
        }
    }
    //通过图片路径保存到相册
    func savePhoto(urlString: String) {
        permissioned()
        guard isPermissioned else { return }
        DispatchQueue.global().async {
            guard let imgUrl = URL(string: urlString) else { return }
            if let imageData = try? Data(contentsOf: imgUrl) {
                let img = UIImage(data: imageData)
                DispatchQueue.main.async {
                    guard img != nil else { return }
                    UIImageWriteToSavedPhotosAlbum(img!, self, #selector(self.saveImage(image:didFinishSavingWithError:contextInfo:)), nil)
                }
            }
        }
    }
    //把图片保存到相册
    func savePhtot(image: UIImage) {
        permissioned()
        guard isPermissioned else { return }
        UIImageWriteToSavedPhotosAlbum(image, self, #selector(self.saveImage(image:didFinishSavingWithError:contextInfo:)), nil)
    }
    
    @objc private func saveImage(image: UIImage, didFinishSavingWithError error: NSError?, contextInfo: AnyObject) {
        if error != nil{
            print("保存失败")
        }else{
            print("保存成功")
        }
    }
    
    //拍照功能
    func startPrimordialCamera(controller: UIViewController) {
        permissioned()
        guard isPermissioned else { return }
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            let picker = UIImagePickerController()
            picker.delegate = self
            picker.sourceType = .camera
            picker.allowsEditing = true
            controller.present(picker, animated: true)
        }
    }
    
    //从相册中选取图片
    func fetchPhotoFromPhotoLibrary(isScan: Bool, controller: UIViewController) {
        permissioned()
        guard isPermissioned else { return }
        self.isScan = isScan
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            let picker = UIImagePickerController()
            picker.delegate = self
            picker.sourceType = .photoLibrary
            picker.allowsEditing = true
            controller.present(picker, animated: true)
        }
    }
    
    //从相册中选取视频
    func fetchVideoFromPhotoLibrary(controller: UIViewController) {
        permissioned()
        guard isPermissioned else { return }
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            let picker = UIImagePickerController()
            picker.delegate = self
            picker.sourceType = .photoLibrary
            picker.mediaTypes = ["public.movie"]
            picker.allowsEditing = false
            controller.present(picker, animated: true)
        }
    }
    //保存图片到文件中
    func savePhotoToDocument(image: UIImage) {
        
        guard let rootPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return }
        let fileManager = FileManager.default
        let filePath = "\(rootPath)/pickedimage.jpg"
        let imageData = image.jpegData(compressionQuality: 1.0)
        fileManager.createFile(atPath: filePath, contents: imageData, attributes: nil)
    }
    //获取图片路径
    func photoFilePath() -> String {
        guard let rootPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        let filePath = "\(rootPath)/pickedimage.jpg"
        return filePath
    }
    
}

extension PhotoManager {
    
    //识别二维码
    func recognizeQRImage(image: UIImage) {
        
        guard let img = CIImage(image: image) else { return }
        let context = CIContext(options: nil)
        let detector = CIDetector(ofType: CIDetectorTypeQRCode, context: context,
                                          options: [CIDetectorAccuracy:CIDetectorAccuracyHigh])
        
        
        guard let features = detector?.features(in: img, options: [CIDetectorAccuracy: CIDetectorAccuracyHigh]) as? [CIQRCodeFeature] else { return }
        
        for feature in features {
            //TODO  扫描结果
            print(feature.messageString ?? "")
        }
    }
    
    //创建二维码图片
    func createQRForString(qrString: String?, qrImageName: String?) -> UIImage? {
        
        guard qrString != nil else { return nil }
        let data = qrString!.data(using: .utf8, allowLossyConversion: false)
        
        let filter = CIFilter(name: "CIQRCodeGenerator")
        filter?.setValue(data, forKey: "inputMessage")
        filter?.setValue("H", forKey: "inputCorrectionLevel")
        
        let qrCIImage = filter?.outputImage
    
        let colorFilter = CIFilter(name: "CIFalseColor")
        colorFilter?.setDefaults()
        colorFilter?.setValue(qrCIImage, forKey: "inputImage")
        colorFilter?.setValue(CIColor(red: 0, green: 0, blue: 0), forKey: "inputColor0")
        colorFilter?.setValue(CIColor(red: 1, green: 1, blue: 1), forKey: "inputColor1")
        
        guard let ciImage = (colorFilter?.outputImage?
            .transformed(by: CGAffineTransform(scaleX: 5, y: 5))) else { return nil }
        let codeImage = UIImage(ciImage: ciImage)
        
        if let qrName = qrImageName, let iconImage = UIImage(named: qrName) {
            let rect = CGRect(x: 0, y: 0, width: codeImage.size.width, height: codeImage.size.height)
            UIGraphicsBeginImageContext(rect.size)
            
            codeImage.draw(in: rect)
            let avatarSize = CGSize(width: rect.size.width * 0.25, height: rect.size.height * 0.25)
            let x = (rect.width - avatarSize.width) * 0.5
            let y = (rect.height - avatarSize.height) * 0.5
            
            iconImage.draw(in: CGRect(x: x, y: y, width: avatarSize.width, height: avatarSize.height))
            let resultImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            return resultImage
        }
        return codeImage
    }
    
    //创建条形码 ZBarSDK
    func generateBarcode(content: String, size: CGSize) -> UIImage? {
        
        guard let barcodeFilter = CIFilter(name: "CICode128BarcodeGenerator") else { return nil }
        // 条形码内容
        barcodeFilter.setValue(content.data(using: .utf8), forKey: "inputMessage")
        // 左右间距
        barcodeFilter.setValue(0, forKey: "inputQuietSpace")
        
        guard let outputImage = barcodeFilter.outputImage else { return nil }
        // 调整图片大小及位置（小数跳转为整数）位置值向下调整，大小只向上调整
        let extent = outputImage.extent.integral
        // 条形码放大 处理模糊
        let scaleX = size.width / extent.width
        let scaleY = size.height / extent.height
        let clearImage = UIImage(ciImage: outputImage.transformed(by: CGAffineTransform(scaleX: scaleX, y: scaleY)))
        return clearImage
    }
    
    //条形码中插入文本
    func insertTextBarcode(text: String, attributes: [NSAttributedString.Key: Any]?, height: CGFloat, barcodeImage: UIImage) -> UIImage? {
        
        let barcodeSize = barcodeImage.size
        // 开启上下文
        UIGraphicsBeginImageContext(CGSize(width: barcodeSize.width, height: barcodeSize.height + 20))
        // 绘制条形码图片
        barcodeImage.draw(in: CGRect(origin: .zero, size: barcodeSize))
        // 文本样式
        let style = NSMutableParagraphStyle()
        style.alignment = .center
        let defaultAttri: [NSAttributedString.Key: Any] = [
            .font: UIFont.systemFont(ofSize: 15),
            .foregroundColor: UIColor.black,
            .kern: 2,
            .paragraphStyle: style
        ]
        let attri = attributes ?? defaultAttri
        // 绘制文本
        (text as NSString).draw(in: CGRect(x: 0, y: barcodeSize.height, width: barcodeSize.width, height: height), withAttributes: attri)
        let outputImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return outputImage
    }
}

extension PhotoManager: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        picker.dismiss(animated: true)
        guard let image = info[UIImagePickerController.InfoKey.editedImage] as? UIImage else { return }
        let aa = UIImage(named: "haah.png")
        if isScan {
            recognizeQRImage(image: aa!)
        } else {
            savePhotoToDocument(image: image)
        }
    }

    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        
        picker.dismiss(animated: true)
    }
}
