package info.bagen.rust.plaoc.system.camera

import android.graphics.drawable.GradientDrawable.Orientation

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

fun CameraImageOption.toCameraSettings(): CameraSettings {
  var settings = CameraSettings()
  settings.resultType = CameraResultType.valueOf(this.resultType)
  settings.quality = this.quality ?: DEFAULT_QUALITY
  settings.shouldResize = this.shouldResize ?: false
  settings.shouldCorrectOrientation = this.shouldCorrectOrientation ?: DEFAULT_CORRECT_ORIENTATION
  settings.saveToGallery = this.saveToGallery ?: DEFAULT_SAVE_IMAGE_TO_GALLERY
  settings.allowEditing = this.allowEditing ?: false
  settings.width = this.width ?: 0
  settings.height = this.height ?: 0
  settings.source = CameraSource.valueOf(this.source ?: "PROMPT")

  return settings
}

data class CameraImageOption(
  var resultType: String = "base64",
  var quality: Int? = 100,
  var shouldResize: Boolean? = false,
  var shouldCorrectOrientation: Boolean? = DEFAULT_CORRECT_ORIENTATION,
  var saveToGallery: Boolean? = DEFAULT_SAVE_IMAGE_TO_GALLERY,
  var allowEditing: Boolean? = false,
  var width: Int? = 0,
  var height: Int? = 0,
  var source: String? = "PROMPT",
)

fun CameraGalleryImageOption.toCameraSettings(): CameraSettings {
  var settings = CameraSettings()
  settings.resultType = CameraResultType.valueOf("base64")
  settings.quality = this.quality ?: DEFAULT_QUALITY
  settings.shouldResize = false
  settings.shouldCorrectOrientation = DEFAULT_CORRECT_ORIENTATION
  settings.saveToGallery = DEFAULT_SAVE_IMAGE_TO_GALLERY
  settings.allowEditing = false
  settings.width = this.width ?: 0
  settings.height = this.height ?: 0
  settings.source = CameraSource.valueOf("PHOTOS")

  return settings
}

data class CameraGalleryImageOption(
  var quality: Int? = DEFAULT_QUALITY,
  var width: Int? = 0,
  var height: Int? = 0,
  var correctOrientation: Boolean? = false
)
