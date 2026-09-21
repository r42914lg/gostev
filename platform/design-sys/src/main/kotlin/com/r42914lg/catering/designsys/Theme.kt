package com.r42914lg.catering.designsys

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

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

private val CateringLightColors = CateringColors(
    background = Paper,
    onBackground = Ink,
    brand = Pine,
    brandLight = PineLight,
    accent = Bronze,
    error = Brick,
    divider = Hairline,
    muted = Muted,
    disabled = OtherMonthDay
)

@Composable
fun CateringTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    CompositionLocalProvider(
        LocalColors provides CateringLightColors,
        LocalSpacing provides CateringSpacing(),
        LocalShapes provides CateringShapes()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
