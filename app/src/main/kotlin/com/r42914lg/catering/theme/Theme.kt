package com.r42914lg.catering.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
    primary = Pine,
    onPrimary = Paper,
    secondary = Bronze,
    onSecondary = Paper,
    tertiary = Brick,
    background = Paper,
    surface = Paper,
    onBackground = Ink,
    onSurface = Ink,
    outline = Hairline,
    surfaceVariant = PineLight,
    onSurfaceVariant = Pine
)

@Composable
fun CateringTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}