package com.r42914lg.catering.mvi

import androidx.compose.runtime.Stable

import com.r42914lg.catering.core.data.model.CalendarEvent

@Stable
internal sealed interface MainEffect {
    data class ShowSnackbar(val messageResId: Int) : MainEffect
    data class OpenEventDetails(val event: CalendarEvent) : MainEffect
}
