package com.r42914lg.catering.usecase

import com.r42914lg.catering.core.data.CalendarDataSource

internal class RefreshCalendarDataUseCase(
    private val calendarDataSource: CalendarDataSource
) {
    suspend operator fun invoke(): Boolean {
        val eventsResult = calendarDataSource.fetchCalendarEvents()
        val assignmentsResult = calendarDataSource.fetchAssignments()
        
        return eventsResult.isSuccess && assignmentsResult.isSuccess
    }
}
