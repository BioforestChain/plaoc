package org.bfchain.libappmgr.ui.dcim

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import org.bfchain.libappmgr.R
import org.bfchain.libappmgr.entity.DCIMInfo
import org.bfchain.libappmgr.entity.DCIMType
import org.bfchain.libappmgr.ui.theme.ColorGrayLevel2
import org.bfchain.libappmgr.ui.theme.ColorGrayLevel5
import org.bfchain.libappmgr.ui.theme.TopBarBackground
import org.bfchain.libappmgr.ui.theme.TopBarSpinnerBG
import org.koin.androidx.compose.koinViewModel

@Composable
private fun getWindowWithDP(): Dp {
  var density = LocalDensity.current.density
  var metrics = LocalContext.current.resources.displayMetrics
  return (metrics.widthPixels / density + 0.5).dp
}

@Composable
fun DCIMItemView(
  dcimInfo: DCIMInfo,
  dcimVM: DCIMViewModel,
  onClick: (DCIMInfo) -> Unit
) {
  Box(
    modifier = Modifier.fillMaxWidth()
  ) {
    AsyncImage(
      model = when (dcimInfo.type) {
        DCIMType.VIDEO -> dcimInfo.bitmap
        else -> dcimInfo.path
      },
      contentDescription = "",
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(getWindowWithDP() / 4)
        .clickable { onClick(dcimInfo) },
    )

    // 在右上角显示是否选中
    CircleCheckBox(
      dcimInfo,
      dcimVM,
      Modifier
        .align(Alignment.TopEnd)
        .padding(4.dp)
        .size(25.dp),
    )

    // 显示 Video的图标和时长
    when (dcimInfo.type) {
      DCIMType.VIDEO -> {
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .width(20.dp)
            .background(Color.Black.copy(0.2f))
        ) {
          Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_video),
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
fun DCIMGridView(dcimVM: DCIMViewModel = koinViewModel(), onClick: (DCIMInfo) -> Unit) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(4),//GridCells.Adaptive(minSize = 60.dp), // 一行四个，或者指定大小
    contentPadding = PaddingValues(4.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp),
    horizontalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    items(dcimVM.dcimInfoList) {
      DCIMItemView(it, dcimVM) { info -> onClick(info) }
    }
  }
}

/**
 * 点击Gird小图片后，就会显示大图
 */
@Composable
fun DCIMInfoViewer(
  dcimVM: DCIMViewModel,
  onBack: () -> Unit
) {
  if (dcimVM.showViewer.value) {
    val showTopbar = remember { mutableStateOf(true) } // 用于判断是否显示TopBar
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          when (showTopbar.value) { // 由于有些纯色的图片跟背景一样，导致看不清楚，这边点击做切换背景
            false -> Color.Black
            true -> Color.White
          }
        )
        .clickable { showTopbar.value = !showTopbar.value }
    ) {
      when (dcimVM.dcimInfo.value.type) {
        DCIMType.IMAGE -> {
          AsyncImage(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.Center),
            model = ImageRequest
              .Builder(LocalContext.current)
              .data(dcimVM.dcimInfo.value.path)
              .decoderFactory(
                SvgDecoder.Factory()
              )
              .crossfade(true)
              .build(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth, // 需要增加modifier才能宽度填充
            alignment = Alignment.TopStart, // 这边表示图片显示开始的位置，默认是center
          )
        }
        DCIMType.GIF -> {
          AsyncImage(
            modifier = Modifier
              .fillMaxSize(),
            model = ImageRequest
              .Builder(LocalContext.current)
              .data(dcimVM.dcimInfo.value.path)
              .decoderFactory(
                GifDecoder.Factory()
              )
              .crossfade(true)
              .build(),
            contentDescription = null
          )
        }
        DCIMType.VIDEO -> {
          AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest
              .Builder(LocalContext.current)
              .data(dcimVM.dcimInfo.value.path)
              .decoderFactory(
                VideoFrameDecoder.Factory()
              )
              .crossfade(true)
              .build(),
            contentDescription = null
          )
          //VideScreen()
        }
      }
      DCIMViewerTopBar(show = showTopbar, dcimVM) {
        dcimVM.showViewer.value = false
        onBack()
      }
      DCIMViewerBottomBar(show = showTopbar, dcimVM)
    }
  }
}

@SuppressLint("RememberReturnType")
@Composable
fun DCIMView(
  dcimVM: DCIMViewModel,
  onGridClick: () -> Unit,
  onViewerClick: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 40.dp)) {
      DCIMGridTopBar(dcimVM)
      Box {
        DCIMGridView(dcimVM) {
          dcimVM.dcimInfo.value = it
          dcimVM.showViewer.value = true
          onGridClick()
        }
        DCIMSpinnerView(dcimVM)
      }
    }
    DCIMGridBottomBar(dcimVM)
    DCIMInfoViewer(dcimVM) { onViewerClick() }
  }
}

/**
 * 照片墙 顶部工具栏
 */
@Composable
fun DCIMGridTopBar(dcimVM: DCIMViewModel) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(50.dp)
      .background(TopBarBackground)
  ) {
    val rotationValue by animateFloatAsState(targetValue = if (dcimVM.showSpinner.value) -180f else 0f)
    Row {
      Image(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
        contentDescription = null,
        modifier = Modifier
          .padding(10.dp, 0.dp, 0.dp, 0.dp)
          .align(CenterVertically)
          .clickable { dcimVM.mCallBack?.cancel() }
      )
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .padding(8.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable { dcimVM.showSpinner.value = !dcimVM.showSpinner.value }
          .background(TopBarSpinnerBG)
      ) {
        Row(
          modifier = Modifier
            .align(Alignment.Center)
            .padding(8.dp, 0.dp, 8.dp, 0.dp)
        ) {
          Text(
            text = dcimVM.dcimSpinner.name,
            color = Color.White,
            modifier = Modifier.align(CenterVertically)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.ic_pop),
            contentDescription = null,
            modifier = Modifier
              .align(CenterVertically)
              .size(20.dp)
              .graphicsLayer { rotationZ = rotationValue }
          )
        }
      }
    }
    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(if (dcimVM.checkedSize.value == 0) TopBarSpinnerBG else Color(0xff00ff00))
    ) {
      TextButton(onClick = {
        var list = arrayListOf<String>()
        dcimVM.checkedList.forEach { list.add(it.path) }
        dcimVM.mCallBack?.send(list)
      }, enabled = dcimVM.checkedList.isNotEmpty()) {
        Text(
          text = if (dcimVM.checkedSize.value == 0) "发送" else "发送 (${dcimVM.checkedSize.value}/${dcimVM.totalSize.value})",
          color = if (dcimVM.checkedSize.value == 0) ColorGrayLevel2 else Color.White
        )
      }
    }
  }
}

@Composable
fun BoxScope.DCIMGridBottomBar(dcimVM: DCIMViewModel) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(40.dp)
      .align(Alignment.BottomStart)
      .background(TopBarBackground)
      .clickable(enabled = dcimVM.checkedSize.value > 0) {
        dcimVM.dcimInfo.value = dcimVM.checkedList[0] // 清空为了显示已选
        dcimVM.showViewer.value = true
      }
  ) {
    Text(
      text = when (dcimVM.checkedSize.value > 0) {
        true -> "预览(${dcimVM.checkedSize.value})"
        false -> "预览"
      },
      color = when (dcimVM.checkedSize.value > 0) {
        true -> Color.White
        false -> ColorGrayLevel5
      },
      modifier = Modifier
        .align(Alignment.CenterStart)
        .padding(6.dp, 0.dp, 0.dp, 0.dp)
    )
  }
}

/**
 * 图片是否选中的圆圈，带数字
 */
@Composable
fun CircleCheckBox(
  dcimInfo: DCIMInfo,
  dcimVM: DCIMViewModel,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.clickable(
      onClick = {
        dcimVM.updateCheckedList(dcimInfo)
      },
      // 去除点击效果
      indication = null,
      interactionSource = remember {
        MutableInteractionSource()
      })
  ) {
    Image(
      modifier = modifier
        .fillMaxSize()
        .background(Color.White, CircleShape),
      painter = rememberAsyncImagePainter(
        model = when (dcimInfo.checked.value) {
          true -> R.drawable.ic_circle_checked //ic_circle_checked2
          false -> R.drawable.ic_circle_unchecked
        }
      ),
      contentDescription = null
    )
    if (dcimInfo.checked.value) {
      Text(
        text = "${dcimInfo.index.value}",
        modifier = Modifier.align(Alignment.Center),
        color = Color.White,
        fontSize = 12.sp
      )
    }
  }
}

/**
 * 大图状态下的TopBar
 */
@Composable
fun BoxScope.DCIMViewerTopBar(
  show: MutableState<Boolean> = mutableStateOf(false),
  dcimVM: DCIMViewModel,
  onBack: () -> Unit
) {
  val density = LocalDensity.current
  AnimatedVisibility(
    visible = show.value,
    enter = slideInVertically {
      // Slide in from 40 dp from the top.
      with(density) { -50.dp.roundToPx() }
    } + expandVertically(
      // Expand from the top.
      expandFrom = Alignment.Top
    ),
    exit = slideOutVertically() + shrinkVertically(),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(TopBarBackground)
    ) {
      Image(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
        contentDescription = null,
        modifier = Modifier
          .align(Alignment.CenterStart)
          .padding(8.dp)
          .clickable { onBack() }
      )
      Text(
        text = "${dcimVM.dcimInfoList.indexOf(dcimVM.dcimInfo.value) + 1} / ${dcimVM.dcimInfoList.size}", //dcimInfo.name,
        modifier = Modifier.align(Alignment.Center),
        color = Color.White
      )
      Box(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(8.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (dcimVM.checkedSize.value == 0) TopBarSpinnerBG else Color(0xff00ff00))
      ) {
        TextButton(onClick = {
          var list = arrayListOf<String>()
          dcimVM.checkedList.forEach { list.add(it.path) }
          dcimVM.mCallBack?.send(list)
        }, enabled = dcimVM.checkedList.isNotEmpty()) {
          Text(
            text = if (dcimVM.checkedSize.value == 0) "发送" else "发送 (${dcimVM.checkedSize.value}/${dcimVM.totalSize.value})",
            color = if (dcimVM.checkedSize.value == 0) ColorGrayLevel2 else Color.White
          )
        }
      }
    }
  }
}

/**
 * 大图状态下的BottomBar
 */
@Composable
fun BoxScope.DCIMViewerBottomBar(
  show: MutableState<Boolean> = mutableStateOf(false),
  dcimVM: DCIMViewModel,
) {
  val density = LocalDensity.current
  AnimatedVisibility(
    visible = show.value,
    modifier = Modifier.align(BottomCenter),
    enter = slideInVertically {
      // Slide in from 40 dp from the top.
      with(density) { 50.dp.roundToPx() }
    } + expandVertically(
      // Expand from the top.
      expandFrom = Alignment.Bottom
    ),
    exit = slideOutVertically() + shrinkVertically(),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(TopBarBackground)
    ) {
      Column {
        // 1. 上面显示LazyHori，，，下面显示
        if (dcimVM.checkedSize.value > 0) {
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .height(60.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(6.dp)
          ) {
            items(dcimVM.checkedList) {
              Box(modifier = Modifier
                .background(
                  if (dcimVM.dcimInfo.value.path == it.path) Color.Black else Color(
                    0x00000000
                  )
                )
                .padding(1.dp)
                .size(50.dp)
                .clickable {
                  dcimVM.dcimInfo.value = it
                }) { // 显示图片
                AsyncImage(
                  model = when (it.type) {
                    DCIMType.VIDEO -> it.bitmap
                    else -> it.path
                  },
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.width(50.dp)
                )
                if (it.type == DCIMType.VIDEO) { // 如果是video的话，需要显示
                  AsyncImage(
                    model = R.drawable.ic_video,
                    contentDescription = null,
                    modifier = Modifier
                      .padding(3.dp)
                      .size(15.dp)
                      .align(Alignment.BottomStart)
                  )
                }
              }
            }
          }
        }
        Spacer(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ColorGrayLevel5)
        )
        // 2. 下面有一条当前状态按钮
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
        ) {
          AsyncImage(
            model = when (dcimVM.dcimInfo.value.checked.value) {
              true -> R.drawable.ic_circle_checked2
              false -> R.drawable.ic_circle_unchecked
            }, contentDescription = null,
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .padding(5.dp, 5.dp, 10.dp, 5.dp)
              .size(20.dp)
              .clickable {
                dcimVM.updateCheckedList(dcimVM.dcimInfo.value)
              }
          )
        }
      }
    }
  }
}

@Composable
fun DCIMSpinnerView(dcimVM: DCIMViewModel) {
  AnimatedVisibility(
    visible = dcimVM.showSpinner.value,
    modifier = Modifier
      .fillMaxWidth(),
    enter = slideInVertically(initialOffsetY = { 0 }) + expandVertically(expandFrom = Alignment.Top),
    exit = slideOutVertically(targetOffsetY = { 0 }) + shrinkVertically(),
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .height(600.dp)
        .background(TopBarBackground),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Log.d("lin.huang", "DCIMSpinnerView11  ->  $dcimVM")
      items(dcimVM.dcimSpinnerList) {
        Row(modifier = Modifier
          .fillMaxWidth()
          .clickable {
            Log.d("lin.huang", "DCIMSpinnerView2222  ->  $dcimVM")
            dcimVM.refreshDCIMInfoList(it)
          }) {
          AsyncImage(
            model = it.path,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(50.dp)
              .align(CenterVertically)
              .padding(10.dp, 4.dp, 10.dp, 4.dp)
          )
          Text(
            text = "${it.name}",
            modifier = Modifier.align(CenterVertically),
            color = Color.White
          )
          Text(
            text = "(${it.count})",
            modifier = Modifier.align(CenterVertically),
            color = ColorGrayLevel5
          )
        }
        Spacer(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ColorGrayLevel5)
        )
      }
    }
  }
}
