package org.bfchain.plaoc.bfs

data class ManifestApp(
    val id: String,
    val versionName: String,
    val versionCode: Int,
    val minBfsVersionCode: Int,
    val keywords: Set<String>,
    val defaultEntry: String,
    val entryResourceMap: Map<String, ManifestEntry>,
) {

}

data class ManifestEntry(val uri: String) {

}