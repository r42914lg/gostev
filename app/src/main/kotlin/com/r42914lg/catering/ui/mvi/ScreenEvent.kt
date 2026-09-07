package com.r42914lg.catering.ui.mvi

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate

@Stable
internal sealed interface ScreenEvent {
    data object PreviousMonthClicked : ScreenEvent
    data object NextMonthClicked : ScreenEvent
    data class DateSelected(val date: LocalDate) : ScreenEvent
}