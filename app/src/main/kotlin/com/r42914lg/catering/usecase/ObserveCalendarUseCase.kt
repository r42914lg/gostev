package com.r42914lg.catering.usecase

import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.model.EventAssignment
import com.r42914lg.catering.mvi.CalendarDay
import com.r42914lg.catering.mvi.EventDot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

internal class ObserveCalendarUseCase(
    private val calendarDataSource: CalendarDataSource
) {
    operator fun invoke(
        currentMonthFlow: Flow<LocalDate>,
        selectedDateFlow: Flow<LocalDate>
    ): Flow<List<CalendarDay>> {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(timeZone)

        return combine(
            currentMonthFlow,
            selectedDateFlow,
            calendarDataSource.calendarEvents,
            calendarDataSource.assignments
        ) { currentMonth, selectedDate, allEvents, myAssignments ->
            val startOffset = currentMonth.dayOfWeek.ordinal % 7
            val startDate = currentMonth.minus(DatePeriod(days = startOffset))

            (0 until 42).map { offset ->
                val date = startDate.plus(DatePeriod(days = offset))
                val isCurrentMonth = date.month == currentMonth.month && date.year == currentMonth.year
                
                val dayEvents = allEvents.filter { it.date == date }
                val dots = dayEvents.map { event ->
                    val assignment = myAssignments.find { it.eventId == event.id }
                    val status = when (assignment?.status) {
                        EventAssignment.Status.CONFIRMED -> EventDot.Status.CONFIRMED
                        EventAssignment.Status.APPLIED -> EventDot.Status.APPLIED
                        null -> EventDot.Status.NOT_REGISTERED
                    }
                    EventDot(
                        status = status,
                        eventId = event.id,
                    )
                }

                CalendarDay(
                    date = date,
                    isCurrentMonth = isCurrentMonth,
                    isToday = date == today,
                    isSelected = date == selectedDate,
                    events = dayEvents,
                    dots = dots,
                )
            }
        }
    }
}
