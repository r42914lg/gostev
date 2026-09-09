package com.r42914lg.catering.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    initialEvents: List<CalendarEvent>,
    initialAssignments: List<EventAssignment>,
    private val calendarDataSource: CalendarDataSource,
    private val supabaseClient: SupabaseClient,
) : ViewModel() {

    private val _events = MutableStateFlow(initialEvents)
    private val _currentIndex = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(false)
    private val _assignments = MutableStateFlow(initialAssignments)

    val state: StateFlow<DetailsState> = combine(
        _events,
        _currentIndex,
        _assignments,
        supabaseClient.auth.sessionStatus,
        _isLoading
    ) { events, index, assignments, authStatus, isLoading ->
        val event = events.getOrNull(index)
        val isAuthorized = authStatus is SessionStatus.Authenticated
        val assignment = event?.let { e -> assignments.find { it.eventId == e.id } }
        
        val status = when (assignment?.status) {
            EventAssignment.Status.CONFIRMED -> DetailsState.Status.CONFIRMED
            EventAssignment.Status.APPLIED -> DetailsState.Status.APPLIED
            else -> DetailsState.Status.NOT_REGISTERED
        }

        DetailsState(
            event = event,
            index = index,
            total = events.size,
            status = status,
            isAuthorized = isAuthorized,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsState()
    )

    init {
        loadAssignments()
    }

    private fun loadAssignments() {
        viewModelScope.launch {
            _assignments.value = calendarDataSource.fetchAssignments().getOrDefault(emptyList())
        }
    }

    fun onAction(action: DetailsAction) {
        when (action) {
            DetailsAction.ApplyClicked -> applyForEvent()
            DetailsAction.CancelClicked -> cancelAssignment()
            DetailsAction.NextClicked -> _currentIndex.update { (it + 1).coerceAtMost(_events.value.size - 1) }
            DetailsAction.PrevClicked -> _currentIndex.update { (it - 1).coerceAtLeast(0) }
        }
    }

    private fun applyForEvent() {
        val eventId = state.value.event?.id ?: return
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            if (calendarDataSource.applyForEvent(userId, eventId)) {
                loadAssignments()
            }
            _isLoading.value = false
        }
    }

    private fun cancelAssignment() {
        val eventId = state.value.event?.id ?: return
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            if (calendarDataSource.cancelEventAssignment(userId, eventId)) {
                loadAssignments()
            }
            _isLoading.value = false
        }
    }
}
