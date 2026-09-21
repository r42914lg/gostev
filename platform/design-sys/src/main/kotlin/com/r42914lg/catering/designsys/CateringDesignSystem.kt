package com.r42914lg.catering.designsys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CateringColors(
    val background: Color,
    val onBackground: Color,
    val brand: Color,
    val brandLight: Color,
    val accent: Color,
    val error: Color,
    val divider: Color,
    val muted: Color,
    val disabled: Color
)

val LocalColors = staticCompositionLocalOf<CateringColors> {
    error("No CateringColors provided")
}

object CateringTheme {
    val colors: CateringColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val spacing: CateringSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val shapes: CateringShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current
}
