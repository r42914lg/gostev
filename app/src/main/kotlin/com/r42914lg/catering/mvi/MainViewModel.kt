package com.r42914lg.catering.mvi

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.UserManager
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

@Stable
internal interface MainStateHolder {
    val screenState: StateFlow<ScreenState>
    fun onScreenAction(event: ScreenEvent)
}

internal class MainViewModel(
    private val calendarDataSource: CalendarDataSource,
    private val userManager: UserManager,
) : ViewModel(), MainStateHolder {

    private val timeZone = TimeZone.currentSystemDefault()
    private var currentMonth = Clock.System.todayIn(timeZone).let {
        LocalDate(it.year, it.month, 1)
    }
    private var selectedDate = Clock.System.todayIn(timeZone)
    private var allEvents = emptyList<CalendarEvent>()
    private var myAssignments = emptyList<EventAssignment>()

    private sealed interface Action {
        data object Load : Action
        data object Refresh : Action
        data class DateSelected(val date: LocalDate) : Action
        data object NextMonth : Action
        data object PrevMonth : Action
    }

    private val actions = MutableSharedFlow<Action>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val screenState: StateFlow<ScreenState> = actions
        .onStart { emit(Action.Load) }
        .flatMapLatest { action -> reduce(action) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = ScreenState(),
        )

    override fun onScreenAction(event: ScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is ScreenEvent.DateSelected -> actions.emit(Action.DateSelected(event.date))
                ScreenEvent.NextMonthClicked -> actions.emit(Action.NextMonth)
                ScreenEvent.PreviousMonthClicked -> actions.emit(Action.PrevMonth)
            }
        }
    }

    private fun reduce(action: Action): Flow<ScreenState> = flow {
        when (action) {
            Action.Load, Action.Refresh -> {
                emit(screenState.value.copyWithIsLoading(true))
                loadData()
                emit(getUpdatedState())
            }
            is Action.DateSelected -> {
                selectedDate = action.date
                emit(getUpdatedState())
            }
            Action.NextMonth -> {
                currentMonth = currentMonth.plus(DatePeriod(months = 1))
                emit(getUpdatedState())
            }
            Action.PrevMonth -> {
                currentMonth = currentMonth.minus(DatePeriod(months = 1))
                emit(getUpdatedState())
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            allEvents = calendarDataSource.fetchCalendarEvents()
            myAssignments = calendarDataSource.fetchAssignments()
        }
    }

    private fun getUpdatedState(): ScreenState {
        val today = Clock.System.todayIn(timeZone)
        val firstDayOfMonth = currentMonth

        val startOffset = firstDayOfMonth.dayOfWeek.ordinal % 7
        val startDate = firstDayOfMonth.minus(DatePeriod(days = startOffset))

        val calendarDays = (0 until 42).map { offset ->
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
                dots = dots,
            )
        }

        return ScreenState(
            isLoading = false,
            monthTitle = currentMonth.month.name.lowercase().replaceFirstChar { c -> c.uppercase() },
            yearTitle = currentMonth.year.toString(),
            days = calendarDays,
        )
    }
}