package info.bagen.rust.plaoc.system.camera

enum class CameraResultType(val type: String) {
  BASE64("base64"), URI("uri"), DATAURL("dataUrl"),
}

enum class CameraSource(val source: String) {
  PROMPT("PROMPT"), CAMERA("CAMERA"), PHOTOS("PHOTOS"),
}

internal val DEFAULT_QUALITY: Int = 90
internal val DEFAULT_SAVE_IMAGE_TO_GALLERY: Boolean = false
internal val DEFAULT_CORRECT_ORIENTATION: Boolean = true

data class CameraSettings(
  var resultType: CameraResultType = CameraResultType.BASE64,
  var quality: Int = DEFAULT_QUALITY,
  var shouldResize: Boolean = false,
  var shouldCorrectOrientation: Boolean = DEFAULT_CORRECT_ORIENTATION,
  var saveToGallery: Boolean = DEFAULT_SAVE_IMAGE_TO_GALLERY,
  var allowEditing: Boolean = false,
  var width: Int = 0,
  var height: Int = 0,
  var source: CameraSource = CameraSource.PROMPT,
)
