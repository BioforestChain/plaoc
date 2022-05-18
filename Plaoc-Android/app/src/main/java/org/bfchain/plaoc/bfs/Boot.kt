package org.bfchain.plaoc.bfs

import android.util.Log
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import org.bfchain.plaoc.dweb.openDWebWindow

private const val TAG = "BFS/Boot"

class Boot(private val activity: ComponentActivity) {
    init {
        val app = ManifestApp(
            "com.bnqkl.bfchain",
            "1.0.0",
            1,
            1,
            setOf(),
            "index",
            mapOf("index" to ManifestEntry("http://172.30.93.161:8080/index.html"))
        )
        val gson = Gson();
        Log.i(TAG, gson.toJson(app))
        Log.i(TAG, gson.fromJson(gson.toJson(app), ManifestApp::class.java).toString())
        openDWebWindow(activity, app.entryResourceMap[app.defaultEntry]!!.uri)
    }

    fun installApp(app: ManifestApp) {

    }
}