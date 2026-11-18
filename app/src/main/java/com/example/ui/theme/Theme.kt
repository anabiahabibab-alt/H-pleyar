package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = HPlayerRedPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A000E),
    onPrimaryContainer = Color(0xFFFFD9DD),
    secondary = HPlayerRedSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF38000B),
    onSecondaryContainer = Color(0xFFFFDADB),
    tertiary = HPlayerRedAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5C0515),
    onTertiaryContainer = Color(0xFFFFE0E3),
    background = HPlayerDarkBackground,
    onBackground = HPlayerTextPrimary,
    surface = HPlayerDarkSurface,
    onSurface = HPlayerTextPrimary,
    surfaceVariant = HPlayerDarkSurfaceVariant,
    onSurfaceVariant = HPlayerTextSecondary,
    outline = HPlayerBorder
)

private val LightColorScheme = DarkColorScheme // Lark Player is universally renowned for its dark media experience

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = LarkDarkBackground.toArgb()
                window.navigationBarColor = LarkDarkBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
