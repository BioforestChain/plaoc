package info.bagen.libappmgr.ui.app

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import info.bagen.libappmgr.R
import info.bagen.libappmgr.ui.download.DownLoadState
import info.bagen.libappmgr.ui.download.DownloadAppMaskView
import info.bagen.libappmgr.ui.download.DownloadDialogView
import info.bagen.libappmgr.ui.view.DialogView

@Composable
private fun BoxScope.AppIcon(appViewState: AppViewState) {
  AsyncImage(
    model = appViewState.iconPath.value,
    contentDescription = null,
    imageLoader = ImageLoader.Builder(LocalContext.current).components {
      add(SvgDecoder.Factory()) // 为了支持 SVG 图片加载
    }.build(),
    modifier = Modifier
      .padding(3.dp)
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp)),
    contentScale = ContentScale.FillWidth,
    placeholder = BitmapPainter(image = ImageBitmap.imageResource(id = R.drawable.ic_launcher)),
    error = BitmapPainter(image = ImageBitmap.imageResource(id = R.drawable.ic_launcher))
  )
}

@Composable
private fun BoxScope.AppName(appViewState: AppViewState) {
  val name = when (appViewState.maskViewState.value.downLoadState) {
    DownLoadState.LOADING -> "下载中"
    DownLoadState.PAUSE -> "暂停"
    DownLoadState.INSTALL -> "正在安装"
    else -> appViewState.name.value
  }
  Text(
    text = name,
    maxLines = 2,
    color = MaterialTheme.colors.onSurface,
    fontSize = 12.sp,
    textAlign = TextAlign.Center,
    modifier = Modifier
      .fillMaxWidth()
      .height(36.dp)
      .align(Alignment.BottomCenter)
  )
}

/**
 * AppView 只存放App的信息，包括图标，字段
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun BoxScope.AppInfoItem(appViewState: AppViewState, onClick: (() -> Unit)? = null) {
  Box(
    modifier = Modifier
      .size(72.dp)
      .align(Alignment.TopCenter)
      .clickable { onClick?.let { it() } }) {
    AppIcon(appViewState)
    if (appViewState.showBadge.value) {
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .size(12.dp)
          .background(Color.Red)
          .align(Alignment.TopEnd)
      )
    }
  }
  AppName(appViewState)
}

@Composable
fun AppInfoView(appViewState: AppViewState, onOpenApp: (() -> Unit)?) {
  val appViewModel = viewModel() as AppViewModel
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(108.dp)
  ) {
    AppInfoItem(appViewState) {
      if (appViewState.isSystemApp.value) {
        onOpenApp?.let { it() }
      } else {
        appViewModel.handleIntent(AppViewIntent.LoadAppNewVersion(appViewState))
      }
    }
    if (appViewState.maskViewState.value.show) {
      DownloadAppMaskView(
        path = appViewState.maskViewState.value.path,
        modifier = Modifier
          .size(66.dp)
          .clip(RoundedCornerShape(12.dp))
          .align(Alignment.TopCenter)
          .background(Color.Black.copy(alpha = 0.6f))
          .padding(6.dp)
      ) { state, dInfo ->
        appViewModel.handleIntent(AppViewIntent.MaskDownloadCallback(state, dInfo, appViewState))
      }
    }
  }
}

@Composable
fun AppInfoGridView(
  appViewModel: AppViewModel, onOpenApp: ((appId: String, url: String) -> Unit)? = null
) {
  val uiState = appViewModel.uiState.value
  Box(modifier = Modifier.fillMaxSize()) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(5),//GridCells.Adaptive(minSize = 60.dp), // 一行五个，或者指定大小
      contentPadding = PaddingValues(8.dp, 8.dp)
    ) {
      items(uiState.appViewStateList) { item ->
        AppInfoView(item) { onOpenApp?.let { it(item.bfsId, item.dAppUrl) } }
      }
    }
  }
  AppDialogView(appViewModel = appViewModel, onOpenApp)
}

@Composable
fun AppDialogView(
  appViewModel: AppViewModel, onOpenApp: ((appId: String, url: String) -> Unit)? = null
) {
  when (appViewModel.uiState.value.appDialogInfo.value.lastType) {
    AppDialogType.DownLoading -> {
      val appViewState = appViewModel.uiState.value.curAppViewState
      DownloadDialogView(
        path = appViewModel.uiState.value.appDialogInfo.value.data as String
      ) { state, dialogInfo ->
        when (state) {
          DownLoadState.COMPLETED, DownLoadState.FAILURE -> {
            appViewModel.handleIntent(
              AppViewIntent.DialogDownloadCallback(state, dialogInfo, appViewState!!)
            )
          }
        }
      }
    }
    AppDialogType.OpenDApp -> {
      appViewModel.uiState.value.curAppViewState?.let { appViewState ->
        onOpenApp?.let { open -> open(appViewState.bfsId, appViewState.dAppUrl) }
      }
      appViewModel.handleIntent(AppViewIntent.DialogHide)
    }
    else -> {
      DialogView(appViewModel.uiState.value.appDialogInfo.value.dialogInfo,
        onConfirm = { appViewModel.handleIntent(AppViewIntent.DialogConfirm) },
        onCancel = { appViewModel.handleIntent(AppViewIntent.DialogHide) })
    }
  }
}
