package org.bfchain.plaoc.dweb

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.bfchain.plaoc.dweb.js.systemUi.DWebIcon
import org.bfchain.plaoc.dweb.js.systemUi.TopBarAction
import org.bfchain.plaoc.dweb.js.util.JsUtil
import org.bfchain.plaoc.webkit.AdWebViewState

@Stable
class TopBarState (
    val title: MutableState<String?>,
    val actions: SnapshotStateList<TopBarAction>,
    val foregroundColor: MutableState<Color>,
    val backgroundColor: MutableState<Color>,
    val height: MutableState<Float>,
    val doBack: () -> Unit,
)

@Composable
fun DWebTopAppBar(
    jsUtil: JsUtil?,
    webViewState: AdWebViewState,
    topBarState:TopBarState,
) {
    val localDensity = LocalDensity.current

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = { topBarState.doBack() },
                modifier = Modifier
//                        .pointerInteropFilter { event ->
//                            Log.i(TAG, "filter NavigationIcon event $event")
//                            true
//                        }
//                        .clickable {
//                            Log.i(TAG, "Clicked NavigationIcon")
//                        }
            ) {
                Icon(Icons.Filled.ArrowBack, "backIcon")
            }
        },
        title = {
            Text(
                text = topBarState.title.value ?: webViewState.pageTitle ?: "",
                modifier = Modifier
//                        .pointerInteropFilter { event ->
//                            Log.i(TAG, "filter Title event $event")
//                            false
//                        }
//                        .clickable {
//                            Log.i(TAG, "Clicked Title")
//                        }
            )
        },
        actions = {
            if (topBarState.actions.size > 0) {
                for (action in topBarState.actions) {
                    IconButton(
                        onClick = {
                            jsUtil?.evalQueue { action.onClickCode }
                        },
                        enabled = !action.disabled
                    ) {
                        DWebIcon(action.icon)
                    }
                }
            }
        },
        colors = @Stable object : TopAppBarColors {
            @Composable
            override fun actionIconContentColor(scrollFraction: Float): State<Color> {
                return topBarState.foregroundColor
            }

            @Composable
            override fun containerColor(scrollFraction: Float): State<Color> {
                return topBarState.backgroundColor
            }

            @Composable
            override fun navigationIconContentColor(scrollFraction: Float): State<Color> {
                return topBarState.foregroundColor
            }

            @Composable
            override fun titleContentColor(scrollFraction: Float): State<Color> {
                return topBarState.foregroundColor
            }
        },
        modifier = Modifier
//                .graphicsLayer(
//                    renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) createBlurEffect(
//                        25f,
//                        25f,
//                        Shader.TileMode.MIRROR
//                    ).asComposeRenderEffect() else null
//                )
            .onGloballyPositioned { coordinates ->
                topBarState.height.value = coordinates.size.height / localDensity.density
            }
//                .pointerInteropFilter { event ->
//                    Log.i(TAG, "filter TopAppBar event $event")
//
//                    // false 会穿透，在穿透后，返回按钮也能点击了
//                    // true 不会穿透，但是返回按钮也无法点击了
//                    false
//                }.clickable {
//                    Log.i(TAG, "Clicked TopAppBar")
//                }
    )
}