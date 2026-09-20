package com.r42914lg.catering.details

import androidx.compose.runtime.Stable

@Stable
sealed interface DetailsEffect {
    data object RegistrationStatusChanged : DetailsEffect
}
