package org.bfchain.plaoc.ui.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import org.bfchain.plaoc.R

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


private val DarkColorWebViewPalette = darkColors(
    primary = WebViewSystemUiColor200,
    primaryVariant = WebViewSystemUiColor700,
    secondary = Teal200
)
private val LightColorWebViewPalette = lightColors(
    primary = WebViewSystemUiColor500,
    primaryVariant = WebViewSystemUiColor700,
    secondary = Teal200
)

fun darkOrLight(darkTheme: Boolean, darkColor: Colors, lightColors: Colors): Colors {
    return if (darkTheme) {
        darkColor
    } else {
        lightColors
    }
}


@Composable
fun PlaocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    context: PlaocTheme = PlaocTheme.Common,
    content: @Composable () -> Unit,
) {
    Log.i(
        "COLOR", "0xFF3700B3~${0xFF3700B3}/${colorResource(id = R.color.purple_200) }"
    )
    val colors = when (context) {
        PlaocTheme.WebView -> darkOrLight(
            darkTheme,
            DarkColorWebViewPalette,
            LightColorWebViewPalette
        )
        else -> darkOrLight(darkTheme, DarkColorPalette, LightColorPalette)
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

enum class PlaocTheme {
    Common(),
    WebView()
}