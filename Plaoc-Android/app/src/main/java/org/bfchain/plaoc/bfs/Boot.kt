package org.bfchain.plaoc.bfs

import android.util.Log
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import org.bfchain.plaoc.openDWebWindow

private const val TAG = "BFS/Boot"

class Boot(private val activity: ComponentActivity) {
    init {
        val manifest = AppManifest(
            "com.bnqkl.bfchain",
            "1.0.0",
            1,
            1,
            setOf(),
            "index",
            mapOf("index" to "http://172.30.93.161:8080/index.html")
        )
        val gson = Gson();
        Log.i(TAG, gson.toJson(manifest))
        openDWebWindow(activity, manifest.enterResourceMap[manifest.defaultEnter]!!)
    }

    fun installApp(manifest: AppManifest) {

    }
}