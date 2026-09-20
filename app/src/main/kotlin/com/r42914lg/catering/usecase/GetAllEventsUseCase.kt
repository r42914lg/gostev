package com.r42914lg.catering.usecase

import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.model.CalendarEvent

internal class GetAllEventsUseCase(
    private val calendarDataSource: CalendarDataSource
) {
    operator fun invoke(): List<CalendarEvent> = calendarDataSource.calendarEvents.value
}
