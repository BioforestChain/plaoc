package org.bfchain.rust.plaoc.system.device.model

data class Location(
  var latitude: Double? = null,
  var longitude: Double? = null,
  var addressLine1: String? = null,
  var city: String? = null,
  var state: String? = null,
  var countryCode: String? = null,
  var postalCode: String? = null
)

class LocationInfo {
}
