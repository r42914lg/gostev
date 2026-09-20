package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import com.r42914lg.catering.core.data.model.Skill
import kotlinx.coroutines.flow.StateFlow

interface CalendarDataSource {
    val calendarEvents: StateFlow<List<CalendarEvent>>
    val assignments: StateFlow<List<EventAssignment>>
    suspend fun fetchCalendarEvents(): Result<Unit>
    suspend fun fetchAssignments(): Result<Unit>
    suspend fun fetchSkillsForEvent(eventId: Long): Result<List<Skill>>
    suspend fun applyForEvent(userId: String, eventId: Long, skills: String?): Boolean
    suspend fun cancelEventAssignment(userId: String, eventId: Long): Boolean
}
