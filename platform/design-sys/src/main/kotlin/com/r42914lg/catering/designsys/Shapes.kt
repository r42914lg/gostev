package com.r42914lg.catering.designsys

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class CateringShapes(
    val card: Shape = RoundedCornerShape(12.dp),
    val button: Shape = RoundedCornerShape(14.dp),
    val pill: Shape = RoundedCornerShape(100.dp)
)

val LocalShapes = staticCompositionLocalOf { CateringShapes() }
