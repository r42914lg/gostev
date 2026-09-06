package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class CalendarDataSource {
    fun observeCalendarEvent(): Flow<List<CalendarEvent>> = flow { }
}