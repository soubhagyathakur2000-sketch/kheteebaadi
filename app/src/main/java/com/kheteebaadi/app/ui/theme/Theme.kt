package com.kheteebaadi.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = ChipBg,
    onPrimaryContainer = GreenDark,
    secondary = Gold,
    onSecondary = Color.White,
    error = RedDanger,
    onError = Color.White,
    background = CreamBg,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = ChipBg,
    onSurfaceVariant = TextMuted,
    outline = OutlineGray,
)

@Composable
fun KheteebaadiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
