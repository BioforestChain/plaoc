package org.bfchain.libappmgr.ui.dcim

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import org.bfchain.libappmgr.entity.DCIMInfo
import org.bfchain.libappmgr.entity.DCIMType

@Composable
private fun getWindowWithDP(): Dp {
  var density = LocalDensity.current.density
  var metrics = LocalContext.current.resources.displayMetrics
  return (metrics.widthPixels / density + 0.5).dp
}

@Composable
fun DCIMItemView(dcimInfo: DCIMInfo, onClick: (DCIMInfo) -> Unit) {
  Box(
    modifier = Modifier.fillMaxWidth()
  ) {
    Image(
      painter = rememberAsyncImagePainter(model = dcimInfo.path),
      contentScale = ContentScale.Crop,
      contentDescription = "",
      modifier = Modifier
        .size(getWindowWithDP() / 4 - 2.dp)
        .clickable { onClick(dcimInfo) },
    )

    when (dcimInfo.checked.value) {
      true -> Text(
        text = "true",
        modifier = Modifier
          .align(Alignment.TopEnd)
          .clickable { dcimInfo.checked.value = false })
      false -> Text(
        text = "false",
        modifier = Modifier
          .align(Alignment.TopEnd)
          .clickable { dcimInfo.checked.value = true })
    }

    when (dcimInfo.type) {
      DCIMType.VIDEO -> {
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .width(20.dp)
        ) {
          Image(
            imageVector = ImageVector.vectorResource(id = org.bfchain.libappmgr.R.drawable.ic_video),
            contentDescription = "",
            modifier = Modifier
              .align(Alignment.CenterVertically)
              .padding(2.dp)
          )
          Text(
            text = String.format(
              "%02d:%02d:%02d",
              dcimInfo.duration / 3600,
              dcimInfo.duration % 3600 / 60,
              dcimInfo.duration % 60
            ),
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
          )
        }
      }
    }
  }
}

@Composable
fun DCIMGridView(dcimInfoList: List<DCIMInfo>, onClick: (DCIMInfo) -> Unit) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(4),//GridCells.Adaptive(minSize = 60.dp), // 一行四个，或者指定大小
    contentPadding = PaddingValues(2.dp)
  ) {
    items(dcimInfoList) {
      DCIMItemView(it) { info -> onClick(info) }
    }
  }
}

@Composable
fun DCIMInfoViewer(
  show: MutableState<Boolean>,
  dcimInfo: MutableState<DCIMInfo>,
  onClick: () -> Unit
) {
  if (show.value) {
    Log.d("lin.huang", "DCIMView showViewer ${dcimInfo.value.path}")
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .clickable {
          show.value = false
          onClick()
        },
      contentAlignment = Alignment.Center
    ) {
      Log.d("lin.huang", "${dcimInfo.value.type}")
      Image(
        painter = rememberAsyncImagePainter(model = dcimInfo.value.path),
        contentScale = ContentScale.FillWidth,
        contentDescription = ""
      )
      /*when (dcimInfo.value.type) {
        DCIMType.IMAGE -> Image(
          painter = rememberAsyncImagePainter(model = dcimInfo.value.path),
          contentScale = ContentScale.FillWidth,
          contentDescription = ""
        )
        DCIMType.GIF -> AsyncImage(
          modifier = Modifier
            .fillMaxSize(),
          model = ImageRequest.Builder(LocalContext.current)
            .data(dcimInfo.value.path)
            .decoderFactory(
              GifDecoder.Factory()
            ).crossfade(true).build(),
          contentDescription = null
        )
        DCIMType.VIDEO -> AsyncImage(
          modifier = Modifier
            .fillMaxSize(),
          model = ImageRequest.Builder(LocalContext.current)
            .data(dcimInfo.value.path)
            .decoderFactory(
              VideoFrameDecoder.Factory()
            ).crossfade(true).build(),
          contentDescription = null
        )
      }*/
    }
  }
}

@SuppressLint("RememberReturnType")
@Composable
fun DCIMView(dcimInfoList: List<DCIMInfo>, onGridClick: () -> Unit, onViewerClick: () -> Unit) {
  Log.d("lin.huang", "DCIMView enter")
  var showViewer = remember { mutableStateOf(false) }
  var curDCIMInfo = remember { mutableStateOf(DCIMInfo("")) }
  Column(modifier = Modifier.fillMaxSize()) {
    /*TopBarView(onBackAction = {}) {

    }*/
    Box(modifier = Modifier.fillMaxSize()) {
      DCIMGridView(dcimInfoList = dcimInfoList) {
        Log.d("lin.huang", "DCIMView callback ${it.path}")
        curDCIMInfo.value = it
        showViewer.value = true
        onGridClick()
      }
      DCIMInfoViewer(show = showViewer, curDCIMInfo) {onViewerClick()}
    }
  }
}
