package com.r42914lg.catering.ui.mvi

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Stable
internal interface MainStateHolder {
    val screenState: StateFlow<ScreenState>
    fun onScreenAction(event: ScreenEvent)
}

internal class MainViewModel(
    private val calendarDataSource: CalendarDataSource,
) : ViewModel(), MainStateHolder {

    private val currentUserId = 1L
    private val timeZone = TimeZone.currentSystemDefault()

    private val _screenState = MutableStateFlow(
        ScreenState(
            monthTitle = "",
            yearTitle = "",
            days = emptyList()
        )
    )
    override val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private var currentMonth = Clock.System.todayIn(timeZone).let {
        LocalDate(it.year, it.month, 1)
    }

    private var selectedDate = Clock.System.todayIn(timeZone)

    private var allEvents = emptyList<CalendarEvent>()
    private var myAssignments = emptyList<EventAssignment>()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            allEvents = calendarDataSource.getCalendarEvents()
            //myAssignments = calendarDataSource.getEventAssignments(currentUserId)
            updateState()
        }
    }

    override fun onScreenAction(event: ScreenEvent) {
        when (event) {
            is ScreenEvent.DateSelected -> onDateSelected(event.date)
            ScreenEvent.NextMonthClicked -> onNextMonth()
            ScreenEvent.PreviousMonthClicked -> onPreviousMonth()
        }
    }

    private fun onPreviousMonth() {
        currentMonth = currentMonth.minus(DatePeriod(months = 1))
        updateState()
    }

    private fun onNextMonth() {
        currentMonth = currentMonth.plus(DatePeriod(months = 1))
        updateState()
    }

    private fun onDateSelected(date: LocalDate) {
        selectedDate = date
        updateState()
    }

    private fun updateState() {
        val today = Clock.System.todayIn(timeZone)
        val firstDayOfMonth = currentMonth

        val startOffset = firstDayOfMonth.dayOfWeek.ordinal % 7
        val startDate = firstDayOfMonth.minus(DatePeriod(days = startOffset))

        val calendarDays = (0 until 42).map { offset ->
            val date = startDate.plus(DatePeriod(days = offset))
            val isCurrentMonth = date.month == currentMonth.month && date.year == currentMonth.year
            
            val dayEvents = allEvents.filter { it.date == date }
            val dots = dayEvents.map { event ->
                val isApplied = myAssignments.any { it.eventId == event.id }
                EventDot(
                    isApplied = isApplied,
                    eventId = event.id,
                )
            }

            CalendarDay(
                date = date,
                isCurrentMonth = isCurrentMonth,
                isToday = date == today,
                isSelected = date == selectedDate,
                dots = dots,
            )
        }

        _screenState.update {
            it.copy(
                monthTitle = currentMonth.month.name.lowercase().replaceFirstChar { c -> c.uppercase() },
                yearTitle = currentMonth.year.toString(),
                days = calendarDays,
            )
        }
    }
}