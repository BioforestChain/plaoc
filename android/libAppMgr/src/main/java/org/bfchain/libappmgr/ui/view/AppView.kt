package org.bfchain.libappmgr.ui.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bfchain.libappmgr.R
import org.bfchain.libappmgr.model.AppInfo

@Composable
fun AppInfoView(appInfo: AppInfo) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(120.dp)
            .padding(6.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
            ) {
                Image(
                    bitmap = BitmapFactory.decodeFile(appInfo.iconPath)?.asImageBitmap()
                        ?: ImageBitmap.imageResource(id = R.drawable.ic_launcher),
                    contentDescription = "icon",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(40.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = appInfo.name,
                    maxLines = 2,
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AppInfoGridView(appInfoList: List<AppInfo>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 60.dp),
        contentPadding = PaddingValues(16.dp, 8.dp)
    ) {
        /*item(span = {
            // this上下文为LazyGridItemSpanScope
            GridItemSpan(this.maxLineSpan)
        }) {
            // CategoryTitle(text = "Vegetables")
            Text(text = "Vegetables")
        }*/

        items(appInfoList) { item ->
            AppInfoView(appInfo = item)
        }
    }

    /*LazyVerticalGrid(
        //cells = GridCells.Adaptive(minSize = 100.dp),
        cells = GridCells.Fixed(count = 5),
        contentPadding = PaddingValues(16.dp, 8.dp)
    ) {
        items(appInfoList) {
            AppInfoView(appInfo = it)
        }
    }*/
    /*LazyColumn {
        items(appInfoList) {
            AppInfoView(appInfo = it)
        }
    }*/
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
