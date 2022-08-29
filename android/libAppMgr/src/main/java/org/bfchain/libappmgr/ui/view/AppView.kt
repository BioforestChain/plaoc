@file:OptIn(ExperimentalFoundationApi::class)

package org.bfchain.libappmgr.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.bfchain.libappmgr.R
import org.bfchain.libappmgr.model.AppInfo

@Composable
fun AppInfoView(appInfo: AppInfo) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(6.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Box(modifier = Modifier.size(60.dp)) {
                Image(
                    ImageVector.vectorResource(id = R.drawable.ic_test),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }
            Text(
                text = appInfo.name,
                maxLines = 2,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun AppInfoGridView(appInfoList: List<AppInfo>) {
    LazyVerticalGrid(
        cells = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(16.dp, 8.dp)
    ) {
        items(appInfoList) {
            AppInfoView(appInfo = it)
        }
    }
}

@Preview
@Composable
fun PreviewAppInfoView() {
    val list: ArrayList<AppInfo> = arrayListOf()
    for (i in 1..30) {
        list.add(
            AppInfo(
                version = "1.0.0",
                bfsAppId = "bsf-app-" + i,
                name = "name + " + i,
                icon = "file:///sys/icon.png",
                author = "",
                homepage = "",
                autoUpdate = null
            )
        )
    }
    AppInfoGridView(appInfoList = list)
}
