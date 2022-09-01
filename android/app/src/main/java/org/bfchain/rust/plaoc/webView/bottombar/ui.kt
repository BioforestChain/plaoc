package org.bfchain.rust.plaoc.webView.bottombar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.bfchain.rust.plaoc.webView.icon.DWebIcon
import org.bfchain.rust.plaoc.webView.jsutil.JsUtil

@Composable
fun DWebBottomBar(
    jsUtil: JsUtil?,
    bottomBarState: BottomBarState,
) {
    val localDensity = LocalDensity.current
    NavigationBar(
        modifier = Modifier
            .let {
              Log.i("xxx","bottomBarState.overlay.value${bottomBarState.overlay.value}")
              bottomBarState.height.value?.let { height ->
                    if (height >= 0) {
                        it.height(height.dp)
                    } else {
                        it
                    }
                } ?: it
            }
            .onGloballyPositioned { coordinates ->
                bottomBarState.height.value = coordinates.size.height / localDensity.density
            },
        containerColor = bottomBarState.backgroundColor.value.copy(bottomBarState.overlay.value ?: 0F),
        contentColor = bottomBarState.foregroundColor.value,
        tonalElevation = 0.dp,
    ) {
      if (bottomBarState.actions.size > 0) {
          var selectedItem by remember(bottomBarState.actions) {
            mutableStateOf(bottomBarState.actions.indexOfFirst { it.selected }
              .let { if (it == -1) 0 else it })
          }

          bottomBarState.actions.forEachIndexed { index, action ->
            // 如果传递了un_source，使用双图片切换
            if (!action.icon.un_source.isNullOrEmpty()) {
              if (selectedItem != index) {
                action.icon.currentSource = action.icon.un_source.toString();
              } else {
                action.icon.currentSource = action.icon.source
              }
            } else {
              action.icon.currentSource = action.icon.source
            }
            NavigationBarItem(
              icon = {
                DWebIcon(action.icon)
              },
              alwaysShowLabel = action.alwaysShowLabel,
              label = if (action.label.isNotEmpty()) {
                { Text(action.label) }
              } else {
                null
              },
              enabled = !action.disabled,
              onClick = {
                if (action.selectable) {
                  selectedItem = index
                }
                jsUtil?.evalQueue { action.onClickCode }
              },
              selected = selectedItem == index,
              colors = action.colors?.toNavigationBarItemColors()
                ?: NavigationBarItemDefaults.colors(),
            )
          }
        }
    }
}
