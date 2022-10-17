package org.bfchain.libappmgr.ui.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import org.bfchain.libappmgr.R
import org.bfchain.libappmgr.entity.AppInfo
import org.bfchain.libappmgr.entity.DownLoadState
import org.bfchain.libappmgr.ui.download.DownloadAppInfoView
import org.bfchain.libappmgr.ui.download.DownloadDialogView
import org.bfchain.libappmgr.utils.FilesUtil

data class AppInfoMode(
  val iconPath: MutableState<String> = mutableStateOf(""),
  val appName: MutableState<String> = mutableStateOf(""),
  val showBadge: MutableState<Boolean> = mutableStateOf(false),
  val downLoadState: MutableState<DownLoadState> = mutableStateOf(DownLoadState.IDLE)
)

fun createAppInfoMode(iconPath: String, appName: String, showBadge: Boolean = false): AppInfoMode {
  var mode = AppInfoMode()
  mode.iconPath.value = iconPath
  mode.appName.value = appName
  mode.showBadge.value = showBadge
  return mode
}

fun AppInfoMode.updateIcon(iconPath: String): AppInfoMode {
  this.iconPath.value = iconPath
  return this
}

fun AppInfoMode.updateName(appName: String): AppInfoMode {
  this.appName.value = appName
  return this
}

fun AppInfoMode.updateShowBadge(show: Boolean): AppInfoMode {
  this.showBadge.value = show
  return this
}

fun AppInfoMode.updateDLState(state: DownLoadState): AppInfoMode {
  this.downLoadState.value = state
  return this
}

@Composable
fun BoxScope.AppIcon(appInfoMode: AppInfoMode) {
  /*Image(
    bitmap = BitmapFactory.decodeFile(appInfoMode.iconPath.value)?.asImageBitmap()
      ?: ImageBitmap.imageResource(id = R.drawable.ic_launcher),
    contentDescription = "icon",
    contentScale = ContentScale.Fit,
    modifier = Modifier
      .padding(3.dp)
      .clip(RoundedCornerShape(12.dp))
      .align(Alignment.Center)
  )*/
  AsyncImage(
    model = appInfoMode.iconPath.value,
    contentDescription = null,
    imageLoader = ImageLoader.Builder(LocalContext.current)
      .components {
        add(SvgDecoder.Factory()) // 为了支持 SVG 图片加载
      }.build(),
    onError = {
      // appInfoMode.iconPath.value = 默认地址
    },
    contentScale = ContentScale.Fit,
    modifier = Modifier
      .padding(3.dp)
      .clip(RoundedCornerShape(12.dp))
      .align(Alignment.Center)
  )

  if (appInfoMode.showBadge.value) {
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(12.dp)
        .background(Color.Red)
        .align(Alignment.TopEnd)
    )
  }
}

fun makeIconFromXMLPath(
  pathStr: String,
  viewportWidth: Float = 24f,
  viewportHeight: Float = 24f,
  defaultWidth: Dp = 24.dp,
  defaultHeight: Dp = 24.dp,
  fillColor: Color = Color.White,
): ImageVector {
  val fillBrush = SolidColor(fillColor)
  val strokeBrush = SolidColor(fillColor)

  return ImageVector.Builder(
    defaultWidth = defaultWidth,
    defaultHeight = defaultHeight,
    viewportWidth = viewportWidth,
    viewportHeight = viewportHeight,
  ).run {
    addPath(
      pathData = addPathNodes(pathStr),
      name = "",
      fill = fillBrush,
      stroke = strokeBrush,
    )
    build()
  }
}

@Composable
fun BoxScope.AppName(appInfoMode: AppInfoMode) {
  Text(
    text = appInfoMode.appName.value,
    maxLines = 2,
    color = Color.White,
    fontSize = 12.sp,
    textAlign = TextAlign.Center,
    modifier = Modifier.fillMaxWidth()
  )
}

/**
 * AppView 只存放App的信息，包括图标，字段
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun AppInfoView(
  appInfoMode: AppInfoMode,
  maskProgressMode: MaskProgressMode = rememberMaskProgressMode(),
  onMaskProgressClick: (() -> Unit)? = null,
  onClick: (() -> Unit)? = null
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(100.dp)
  ) {
    Box(modifier = Modifier
      .width(66.dp)
      .height(66.dp)
      .align(Alignment.TopCenter)
      .clickable {
        onClick?.let { onClick() }
      }
    ) {
      AppIcon(appInfoMode)
      MaskProgressView(maskProgressMode, onMaskProgressClick)
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(36.dp)
        .padding(0.dp, 3.dp, 0.dp, 0.dp)
        .align(Alignment.BottomCenter)
    ) {
      AppName(appInfoMode)
    }
  }
}

@Composable
fun AppInfoGridView(
  appInfoList: List<AppInfo>,
  downModeDialog: Boolean = false,
  onOpenApp: ((appId: String, url: String) -> Unit)? = null
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(5),//GridCells.Adaptive(minSize = 60.dp), // 一行五个，或者指定大小
    contentPadding = PaddingValues(8.dp, 8.dp)
  ) {
    items(appInfoList) { item ->
      when (downModeDialog) {
        true -> DownloadDialogView(appInfo = item, onOpenApp)
        false -> DownloadAppInfoView(appInfo = item, onOpenApp)
      }
    }
  }
}

@Preview(name = "AppGridView")
@Composable
fun PreviewAppInfoView() {
  val list: ArrayList<AppInfoMode> = arrayListOf()
  for (i in 1..30) {
    list.add(
      createAppInfoMode("", "name +++ $i")
    )
  }
  LazyVerticalGrid(
    columns = GridCells.Fixed(5),//GridCells.Adaptive(minSize = 60.dp), // 一行五个，或者指定大小
    contentPadding = PaddingValues(8.dp, 8.dp)
  ) {
    items(list) { item ->
      AppInfoView(appInfoMode = item)
    }
  }
}
