package org.bfchain.plaoc.dweb

import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.bfchain.plaoc.dweb.js.systemUi.BottomBarAction
import org.bfchain.plaoc.dweb.js.systemUi.DWebIcon
import org.bfchain.plaoc.dweb.js.util.JsUtil

@Stable
class BottomBarState(
    val enabled: MutableState<Boolean?>,
    val overlay: MutableState<Boolean>,
    val height: MutableState<Float?>,
    val actions: SnapshotStateList<BottomBarAction>,
    val backgroundColor: MutableState<Color>,
    val foregroundColor: MutableState<Color>,
) {
    val isEnabled: Boolean
        get() {
            return if (enabled.value == null) {
                actions.size > 0
            } else {
                enabled.value as Boolean
            }
        }

    companion object {
        @Composable
        fun Default(): BottomBarState {
            val bottomBarBackgroundColor =
                MaterialTheme.colors.primarySurface.let { defaultPrimarySurface ->
                    remember {
                        mutableStateOf(defaultPrimarySurface)
                    }
                }
            val bottomBarForegroundColor =
                MaterialTheme.colors.contentColorFor(bottomBarBackgroundColor.value)
                    .let { defaultContentColor ->
                        remember {
                            mutableStateOf(defaultContentColor)
                        }
                    }
            return remember {
                BottomBarState(
                    enabled = mutableStateOf(null),
                    overlay = mutableStateOf(false),
                    height = mutableStateOf(null),
                    actions = mutableStateListOf(),
                    backgroundColor = bottomBarBackgroundColor,
                    foregroundColor = bottomBarForegroundColor,
                )
            }
        }
    }
}

@Composable
fun DWebBottomBar(
    jsUtil: JsUtil?,
    bottomBarState: BottomBarState,
) {
    val localDensity = LocalDensity.current;
    NavigationBar(
        modifier = Modifier
            .let {
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
        containerColor = bottomBarState.backgroundColor.value,
        contentColor = bottomBarState.foregroundColor.value,
        tonalElevation = 0.dp,
    ) {
        if (bottomBarState.actions.size > 0) {
            var selectedItem by remember(bottomBarState.actions) {
                mutableStateOf(bottomBarState.actions.indexOfFirst { it.selected }
                    .let { if (it == -1) 0 else it })
            }
            bottomBarState.actions.forEachIndexed { index, action ->
                NavigationBarItem(
                    icon = {
                        DWebIcon(action.icon)
                    },
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