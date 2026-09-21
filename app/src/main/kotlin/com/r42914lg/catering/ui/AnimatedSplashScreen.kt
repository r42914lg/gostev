package com.r42914lg.catering.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.r42914lg.catering.R
import com.r42914lg.catering.designsys.CateringTheme
import kotlinx.coroutines.delay

@Composable
internal fun AnimatedSplashScreen(
    onAnimationFinished: () -> Unit
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        )
        delay(300)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CateringTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.splash_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = CateringTheme.colors.brand,
            modifier = Modifier.rotate(rotation.value)
        )
    }
}
