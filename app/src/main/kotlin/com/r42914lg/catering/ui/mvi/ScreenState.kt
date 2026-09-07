package com.r42914lg.catering.ui.mvi

import kotlinx.datetime.LocalDate

internal data class ScreenState(
    val monthTitle: String,
    val yearTitle: String,
    val days: List<CalendarDay> = emptyList(),
)

internal data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val dots: List<EventDot>
)

internal data class EventDot(
    val isApplied: Boolean,
    val eventId: Long,
)