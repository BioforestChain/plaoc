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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
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
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import org.bfchain.libappmgr.R
import org.bfchain.libappmgr.entity.DCIMInfo
import org.bfchain.libappmgr.entity.DCIMType
import org.bfchain.libappmgr.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
private fun getWindowWithDP(): Dp {
  val density = LocalDensity.current.density
  val metrics = LocalContext.current.resources.displayMetrics
  return (metrics.widthPixels / density + 0.5).dp
}

@Composable
fun DCIMItemView(
  dcimInfo: DCIMInfo,
  dcimVM: DCIMViewModel,
  onClick: (DCIMInfo) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(getWindowWithDP() / 4)
  ) {
    AsyncImage(
      model = when (dcimInfo.type) {
        DCIMType.VIDEO -> dcimInfo.bitmap
        else -> dcimInfo.path
      },
      contentDescription = "",
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(dcimInfo) },
    )

    if (dcimInfo.checked.value) { // 如果是被选中了，增加一个半透明遮罩
      Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.2f)))
    }

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
            .height(DimenVideoDurationHeight)
            .background(Color.Black.copy(0.2f))
        ) {
          Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_video),
            contentDescription = "",
            modifier = Modifier
              .align(CenterVertically)
              .padding(2.dp)
          )
          val duration = dcimInfo.duration.value
          if (duration > 0) {
            Text(
              text = String.format(
                "%02d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60
              ),
              color = Color.White,
              fontSize = 14.sp,
              modifier = Modifier.align(CenterVertically)
            )
          }
        }
      }
      else -> {
        null
      }
    }
  }
}

@Composable
fun DCIMGridView(dcimVM: DCIMViewModel = koinViewModel(), onClick: (DCIMInfo) -> Unit) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(4),//GridCells.Adaptive(minSize = 60.dp), // 一行四个，或者指定大小
    //contentPadding = PaddingValues(4.dp, 0.dp, 4.dp, 0.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp),
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    modifier = Modifier.background(ColorBackgroundBar)
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
  onViewerClick: () -> Unit
) {
  if (dcimVM.showViewer.value) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(//Color.Black
          when (dcimVM.dcimInfo.value.type != DCIMType.VIDEO && dcimVM.showViewerBar.value) { // 由于有些纯色的图片跟背景一样，导致看不清楚，这边点击做切换背景
            false -> Color.Black
            true -> Color.White
          }
        )
        .clickable(
          onClick = {
            dcimVM.showViewerBar.value = !dcimVM.showViewerBar.value
            onViewerClick()
          },
          // 去除水波纹效果
          indication = null,
          interactionSource = remember { MutableInteractionSource() }
        )
    ) {
      DCIMHorizontalPager(dcimVM = dcimVM)
      DCIMViewerTopBar(dcimVM) {
        dcimVM.showViewer.value = false
      }
      DCIMViewerBottomBar(dcimVM)
    }
  } else {
    dcimVM.showViewerBar.value = true
    if (dcimVM.showPreview.value) {
      dcimVM.showPreview.value = false
      // 为了将之前预览状态下移除的列表删掉
      var tempList = arrayListOf<DCIMInfo>()
      dcimVM.checkedList.forEach {
        if (it.overlay.value) {
          it.overlay.value = false
          it.checked.value = false
          tempList.add(it)
        }
      }
      dcimVM.checkedList.removeAll(tempList)
      dcimVM.updateCheckedList()
    }
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DCIMHorizontalPager(dcimVM: DCIMViewModel) {
  var exists = false
  var checkPoint = 0
  var curShowlist = if (dcimVM.showPreview.value) {
    dcimVM.checkedList
  } else {
    dcimVM.dcimInfoList
  }
  for (dcimInfo in curShowlist) {
    if (dcimInfo.path == dcimVM.dcimInfo.value.path) {
      exists = true
      break
    }
    checkPoint++
  }
  if (exists) {
    var pagerState = PagerState(checkPoint)
    // 监听页面更改, 使用 snapshotFlow 函数来观察流的变化
    LaunchedEffect(pagerState) {
      snapshotFlow { pagerState.currentPage }.collect { page ->
        dcimVM.dcimInfo.value = curShowlist[page]
      }
    }
    HorizontalPager(
      count = curShowlist.size,
      state = pagerState,
      modifier = Modifier.fillMaxSize()
    ) { loadPage ->
      // 该区块有三个参数：currentPage表示当前显示的界面，currentPageOffset表示滑动偏移量，loadPage表示加载界面
      if (loadPage >= 0 && loadPage < curShowlist.size) {
        DCIMPager(dcimVM = dcimVM, curDCIMInfo = curShowlist[loadPage])
      }
    }
  } else {
    DCIMPager(dcimVM = dcimVM, curDCIMInfo = dcimVM.dcimInfo.value)
  }
}

@Composable
fun DCIMPager(dcimVM: DCIMViewModel, curDCIMInfo: DCIMInfo) {
  Box(modifier = Modifier.fillMaxSize()) {
    when (curDCIMInfo.type) {
      DCIMType.IMAGE -> {
        AsyncImage(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center),
          model = ImageRequest
            .Builder(LocalContext.current)
            .data(curDCIMInfo.path)
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
            .data(curDCIMInfo.path)
            .decoderFactory(
              GifDecoder.Factory()
            )
            .crossfade(true)
            .build(),
          contentDescription = null
        )
      }
      DCIMType.VIDEO -> {
        VideScreen(dcimVM, curDCIMInfo.path)
      }
      DCIMType.OTHER -> {
        throw UnknownError()
      }
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
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ColorBackgroundBar)
  ) {
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
      .height(DimenTopBarHeight)
      .background(ColorBackgroundBar)
      .padding(DimenBarLeftPadding, 0.dp, DimenBarRightPadding, 0.dp)
  ) {
    val rotationValue by animateFloatAsState(targetValue = if (dcimVM.showSpinner.value) -180f else 0f)
    Row {
      Image(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
        contentDescription = null,
        modifier = Modifier
          .align(CenterVertically)
          .clickable { dcimVM.mCallBack?.cancel() }
      )
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .padding(8.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable { dcimVM.showSpinner.value = !dcimVM.showSpinner.value }
          .background(ColorBackgroundSearch)
      ) {
        Row(
          modifier = Modifier
            .align(Alignment.Center)
            .padding(8.dp, 0.dp, 8.dp, 0.dp)
        ) {
          Text(
            text = dcimVM.dcimSpinner.name,
            color = Color.White,
            fontSize = 14.sp,
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
        .background(if (dcimVM.checkedList.size == 0) ColorSendButtonGray else ColorSendButtonGreen)
    ) {
      TextButton(onClick = {
        var list = arrayListOf<String>()
        dcimVM.checkedList.forEach { list.add(it.path) }
        dcimVM.mCallBack?.send(list)
      }, enabled = dcimVM.checkedList.isNotEmpty()) {
        Text(
          text = if (dcimVM.checkedList.size == 0) "发送" else "发送 (${dcimVM.checkedList.size}/${dcimVM.dcimInfoList.size})",
          color = if (dcimVM.checkedList.size == 0) ColorGrayLevel2 else Color.White
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
      .height(DimenBottomBarHeight)
      .align(Alignment.BottomStart)
      .background(ColorBackgroundBar)
      .padding(DimenBarLeftPadding, 0.dp, DimenBarRightPadding, 0.dp)
      .clickable(enabled = dcimVM.checkedList.size > 0) {
        dcimVM.dcimInfo.value = dcimVM.checkedList[0] // 清空为了显示已选
        dcimVM.showViewer.value = true
        dcimVM.showPreview.value = true // 显示的内容是预览
      }
  ) {
    Text(
      text = when (dcimVM.checkedList.size > 0) {
        true -> "预览(${dcimVM.checkedList.size})"
        false -> "预览"
      },
      color = when (dcimVM.checkedList.size > 0) {
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
      modifier = modifier,
      painter = rememberAsyncImagePainter(
        model = if (dcimInfo.checked.value) {
          R.drawable.ic_circle_checked //ic_circle_checked2
        } else {
          R.drawable.ic_circle_uncheck
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
  dcimVM: DCIMViewModel,
  onBack: () -> Unit
) {
  val density = LocalDensity.current
  AnimatedVisibility(
    visible = dcimVM.showViewerBar.value,
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
        .height(DimenTopBarHeight)
        .background(ColorBackgroundBar)
        .padding(DimenBarLeftPadding, 0.dp, DimenBarRightPadding, 0.dp)
    ) {
      Image(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
        contentDescription = null,
        modifier = Modifier
          .align(Alignment.CenterStart)
          .clickable { onBack() }
      )
      var title = if (dcimVM.showPreview.value) {
        "${dcimVM.checkedList.indexOf(dcimVM.dcimInfo.value) + 1} / ${dcimVM.checkedList.size}"
      } else {
        "${dcimVM.dcimInfoList.indexOf(dcimVM.dcimInfo.value) + 1} / ${dcimVM.dcimInfoList.size}"
      }
      Text(
        text = title, //dcimInfo.name,
        modifier = Modifier.align(Alignment.Center),
        color = Color.White
      )
      Box(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(8.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (dcimVM.checkedList.size == 0) ColorSendButtonGray else ColorSendButtonGreen)
      ) {
        TextButton(onClick = {
          var list = arrayListOf<String>()
          dcimVM.checkedList.forEach { list.add(it.path) }
          dcimVM.mCallBack?.send(list)
        }, enabled = dcimVM.checkedList.isNotEmpty()) {
          Text(
            text = if (dcimVM.checkedList.size == 0) "发送" else "发送 (${dcimVM.checkedList.size}/${dcimVM.dcimInfoList.size})",
            color = if (dcimVM.checkedList.size == 0) ColorGrayLevel2 else Color.White
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
  dcimVM: DCIMViewModel,
) {
  val density = LocalDensity.current
  AnimatedVisibility(
    visible = dcimVM.showViewerBar.value,
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
        .background(ColorBackgroundBar.copy(0.8f))
    ) {
      Column {
        // 1. 上面显示LazyHorizontal，，，下面显示
        if (dcimVM.checkedList.size > 0) {
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .height(DimenBarPreviewHeight),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(5.dp, 10.dp, 5.dp, 10.dp)
          ) {
            items(dcimVM.checkedList) { dcimItem ->
              Box(modifier = Modifier
                /*.background(
                  if (dcimVM.dcimInfo.value.path == dcimItem.path) Color.Green
                  else ColorBackgroundBar
                )
                .padding(1.dp)
                .background(ColorBackgroundBar)*/
                .size(DimenBottomBarHeight)
                .clickable {
                  if (dcimVM.showPreview.value) {
                    dcimVM.dcimInfo.value = dcimItem
                  } else { // 如果不是点击预览，会存在选中的列表不在当前spinner加载的列表中，不允许点击
                    dcimVM.dcimInfoList.forEach { dcimInfo ->
                      if (dcimItem.path == dcimInfo.path) {
                        dcimVM.dcimInfo.value = dcimItem
                        return@clickable
                      }
                    }
                  }
                }) { // 显示图片
                if (dcimVM.dcimInfo.value.path == dcimItem.path) {
                  AsyncImage(
                    model = R.drawable.ic_rectangle,
                    contentDescription = "",
                    modifier = Modifier
                      .width(DimenBottomBarHeight)
                      .align(Center)
                  )
                }
                AsyncImage(
                  model = when (dcimItem.type) {
                    DCIMType.VIDEO -> dcimItem.bitmap
                    else -> dcimItem.path
                  },
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .width(DimenBottomBarHeight)
                    .align(Center)
                    .padding(1.dp)
                )
                // 增加一层白色遮罩
                if (dcimItem.overlay.value) {
                  AsyncImage(
                    model = "",
                    contentDescription = "",
                    modifier = Modifier.background(Color.White.copy(0.6f))
                  )
                }
                if (dcimItem.type == DCIMType.VIDEO) { // 如果是video的话，需要显示
                  AsyncImage(
                    model = R.drawable.ic_video,
                    contentDescription = null,
                    modifier = Modifier
                      .padding(4.dp)
                      .size(15.dp)
                      .align(Alignment.BottomStart)
                  )
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
        }
        // 2. 下面有一条当前状态按钮
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(DimenBottomBarHeight)
        ) {
          AsyncImage(
            model = if (dcimVM.dcimInfo.value.checked.value) {
              R.drawable.ic_circle_checked2
            } else {
              R.drawable.ic_circle_uncheck
            }, contentDescription = null,
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .padding(5.dp, 5.dp, 10.dp, 5.dp)
              .size(20.dp)
              .clickable {
                if (dcimVM.showPreview.value) { // 如果是”预览“模式下，点击不直接删除，而是增加一个遮罩
                  dcimVM.dcimInfo.value.checked.value = !dcimVM.dcimInfo.value.checked.value
                  dcimVM.dcimInfo.value.overlay.value = !dcimVM.dcimInfo.value.checked.value
                } else {
                  dcimVM.updateCheckedList(dcimVM.dcimInfo.value)
                }
              }
          )
        }
      }
    }
  }
}

@Composable
fun DCIMSpinnerView(dcimVM: DCIMViewModel) {
  Log.d("lin.huang", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa ${dcimVM.dcimSpinnerList.size}")
  if (dcimVM.dcimSpinnerList.isEmpty()) return
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
        .height(DimenSpinnerViewHeight)
        .background(ColorBackgroundBar),
      verticalArrangement = Arrangement.spacedBy(DimenGridArrangementSpace)
    ) {
      items(dcimVM.dcimSpinnerList) {
        Row(modifier = Modifier
          .fillMaxWidth()
          .clickable {
            dcimVM.refreshDCIMInfoList(it)
          }) {
          AsyncImage(
            model = it.path.value,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(DimenTopBarHeight)
              .align(CenterVertically)
              .padding(10.dp, 4.dp, 10.dp, 4.dp)
          )
          Text(
            text = it.name,
            modifier = Modifier.align(CenterVertically),
            color = Color.White
          )
          Text(
            text = "(${it.count.value})",
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
