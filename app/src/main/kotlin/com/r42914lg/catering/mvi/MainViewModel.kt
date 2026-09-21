package com.r42914lg.catering.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.catering.banners.usecase.ObserveBannersUseCase
import com.r42914lg.catering.core.data.UserManager
import com.r42914lg.catering.core.data.model.Banner
import com.r42914lg.catering.core.data.model.User
import com.r42914lg.catering.usecase.GetAllEventsUseCase
import com.r42914lg.catering.usecase.ObserveCalendarUseCase
import com.r42914lg.catering.usecase.ObserveForceUpdateUseCase
import com.r42914lg.catering.usecase.RefreshCalendarDataUseCase
import com.r42914lg.catering.utils.Event
import com.r42914lg.catering.utils.combine
import com.r42914lg.catering.utils.eventFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import androidx.compose.runtime.Stable

@Stable
internal interface MainStateHolder {
    val screenState: StateFlow<ScreenState>
    val effects: Flow<Event<MainEffect>>
    fun onScreenAction(event: ScreenEvent)
}

internal class MainViewModel(
    userManager: UserManager,
    observeBannersUseCase: ObserveBannersUseCase,
    observeForceUpdateUseCase: ObserveForceUpdateUseCase,
    observeCalendarUseCase: ObserveCalendarUseCase,
    private val refreshCalendarDataUseCase: RefreshCalendarDataUseCase,
    private val getAllEventsUseCase: GetAllEventsUseCase,
) : ViewModel(), MainStateHolder {

    private val timeZone = TimeZone.currentSystemDefault()
    private val currentMonthFlow = MutableStateFlow(
        Clock.System.todayIn(timeZone).let { LocalDate(it.year, it.month, 1) }
    )
    private val selectedDateFlow = MutableStateFlow(Clock.System.todayIn(timeZone))

    private sealed interface Action {
        data object Load : Action
        data class Refresh(val showSpinner: Boolean = true) : Action
        data class DateSelected(val date: LocalDate) : Action
        data object NextMonth : Action
        data object PrevMonth : Action
    }

    private val actions = MutableSharedFlow<Action>()
    private val _effects = eventFlow<MainEffect>()
    override val effects: Flow<Event<MainEffect>> = _effects

    @OptIn(ExperimentalCoroutinesApi::class)
    override val screenState: StateFlow<ScreenState> = combine(
        actions
            .onStart { emit(Action.Load) }
            .flatMapLatest { action -> reduce(action) },
        userManager.userData,
        observeBannersUseCase()
            .onStart { emit(emptyList<Banner>() to "") },
        observeForceUpdateUseCase(),
        observeCalendarUseCase(currentMonthFlow, selectedDateFlow),
        currentMonthFlow,
    ) {
        reducedState: ScreenState,
        userData: User?,
        bannersInfo: Pair<List<Banner>, String>,
        isUpdateRequired: Boolean, days: List<CalendarDay>,
        currentMonth: LocalDate ->

        val (banners, bannersBaseUrl) = bannersInfo
        ScreenState(
            isLoading = reducedState.isLoading,
            userName = userData?.name,
            monthTitle = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() },
            yearTitle = currentMonth.year.toString(),
            days = days,
            banners = banners,
            bannersBaseUrl = bannersBaseUrl,
            isUpdateRequired = isUpdateRequired
        )
    }.stateIn(
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
                is ScreenEvent.RefreshRequested -> actions.emit(Action.Refresh(event.showSpinner))
                is ScreenEvent.BannerClicked -> {
                    val clickedEvent = getAllEventsUseCase().find { it.id == event.eventId }
                    if (clickedEvent != null) {
                        _effects.emit(Event(MainEffect.OpenEventDetails(clickedEvent)))
                    }
                }
            }
        }
    }

    private fun reduce(action: Action): Flow<ScreenState> = flow {
        when (action) {
            Action.Load, is Action.Refresh -> {
                if (action !is Action.Refresh || action.showSpinner) {
                    emit(ScreenState(isLoading = true))
                }
                if (!refreshCalendarDataUseCase()) {
                    _effects.tryEmit(
                        Event(MainEffect.ShowSnackbar("Could not load calendar data"))
                    )
                }
                emit(ScreenState(isLoading = false))
            }
            is Action.DateSelected -> {
                selectedDateFlow.value = action.date
                emit(ScreenState(isLoading = false))
            }
            Action.NextMonth -> {
                currentMonthFlow.update { it.plus(DatePeriod(months = 1)) }
                emit(ScreenState(isLoading = false))
            }
            Action.PrevMonth -> {
                currentMonthFlow.update { it.minus(DatePeriod(months = 1)) }
                emit(ScreenState(isLoading = false))
            }
        }
    }
}
