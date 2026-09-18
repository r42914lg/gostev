package com.r42914lg.catering.mvi

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate

@Stable
internal sealed interface ScreenEvent {
    data object PreviousMonthClicked : ScreenEvent
    data object NextMonthClicked : ScreenEvent
    data class DateSelected(val date: LocalDate) : ScreenEvent
    data object RefreshRequested : ScreenEvent
    data class BannerClicked(val eventId: Long) : ScreenEvent
}