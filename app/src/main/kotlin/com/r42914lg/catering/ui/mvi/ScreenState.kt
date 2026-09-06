package com.r42914lg.catering.ui.mvi

import com.r42914lg.catering.core.data.model.CalendarEvent

data class ScreenState(
    val month: String,
    val visitors: List<CalendarEvent> = emptyList(),
)