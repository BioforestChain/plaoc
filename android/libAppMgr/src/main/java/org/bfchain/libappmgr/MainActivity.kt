package org.bfchain.libappmgr

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bfchain.libappmgr.ui.theme.AppMgrTheme
import org.bfchain.libappmgr.ui.view.PreviewAppInfoView
import org.bfchain.libappmgr.utils.FilesUtil

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppMgrTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary)
                ) {
                    Gretting(name = "Compose!!", this@MainActivity)
                    //PreviewAppInfoView()
                }
            }
        }
    }
}

@Composable
fun Gretting(name: String, context: Context) {
    Text(text = "Hello $name")
    //FilesUtil.traverseAppDirectory(context, FilesUtil.APP_DIR_TYPE.RememberApp)
    FilesUtil.copyAssetsToRememberAppDir(context)
}
