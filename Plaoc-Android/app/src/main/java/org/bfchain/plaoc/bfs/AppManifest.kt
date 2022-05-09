package org.bfchain.plaoc.bfs

data class AppManifest(
    val id: String,
    val versionName: String,
    val versionCode: Int,
    val minBfsVersionCode: Int,
    val keywords: Set<String>,
    val defaultEnter: String,
    val enterResourceMap: Map<String, String>,
) {

}