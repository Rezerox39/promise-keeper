package com.promisekeeper.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = BlackBase,
    primaryContainer = BlackSurface3,
    onPrimaryContainer = GreenPrimary,
    secondary = GreenSecondary,
    onSecondary = BlackBase,
    secondaryContainer = BlackSurface4,
    onSecondaryContainer = GreenSecondary,
    tertiary = GreenMuted,
    onTertiary = BlackBase,
    background = BlackBase,
    onBackground = TextPrimary,
    surface = BlackSurface1,
    onSurface = TextPrimary,
    surfaceVariant = BlackSurface2,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderLight,
    error = ErrorColor,
    onError = Color.White,
    errorContainer = Color(0x1AFF5544),
    onErrorContainer = ErrorColor
)

@Composable
fun PromiseKeeperTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PromiseTypography,
        content = content
    )
}
