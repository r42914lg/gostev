package com.r42914lg.catering.mvi

import kotlinx.datetime.LocalDate

internal data class ScreenState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val monthTitle: String = "",
    val yearTitle: String = "",
    val days: List<CalendarDay> = emptyList(),
) {
    fun copyWithIsLoading(isLoading: Boolean) = copy(isLoading = isLoading)
}

internal data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val dots: List<EventDot>
)

internal data class EventDot(
    val status: Status,
    val eventId: Long,
) {
    enum class Status { NOT_REGISTERED, APPLIED, CONFIRMED }
}