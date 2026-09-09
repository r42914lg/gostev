package com.r42914lg.catering.mvi

import androidx.compose.runtime.Stable

@Stable
internal sealed interface MainEffect {
    data class ShowSnackbar(val message: String) : MainEffect
}
