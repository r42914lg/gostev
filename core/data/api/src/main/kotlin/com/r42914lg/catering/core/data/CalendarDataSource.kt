package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import kotlinx.coroutines.flow.StateFlow

interface CalendarDataSource {
    val assignments: StateFlow<List<EventAssignment>>
    suspend fun fetchCalendarEvents(): Result<List<CalendarEvent>>
    suspend fun fetchAssignments(): Result<Unit>
    suspend fun applyForEvent(userId: String, eventId: Long): Boolean
    suspend fun cancelEventAssignment(userId: String, eventId: Long): Boolean
}
