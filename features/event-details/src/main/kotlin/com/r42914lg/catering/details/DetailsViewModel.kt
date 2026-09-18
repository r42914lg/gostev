package com.r42914lg.catering.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import com.r42914lg.catering.core.data.model.Skill
import com.r42914lg.catering.core.utils.combine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    initialEvents: List<CalendarEvent>,
    private val calendarDataSource: CalendarDataSource,
    private val supabaseClient: SupabaseClient,
) : ViewModel() {

    private val _events = MutableStateFlow(initialEvents)
    private val _currentIndex = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(false)
    private val _availableSkills = MutableStateFlow<List<Skill>>(emptyList())
    private val _selectedSkillIds = MutableStateFlow<Set<Long>>(emptySet())

    private val _effects = MutableSharedFlow<DetailsEffect>()
    val effects: Flow<DetailsEffect> = _effects.asSharedFlow()

    val state: StateFlow<DetailsState> = combine(
        _events,
        _currentIndex,
        calendarDataSource.assignments,
        supabaseClient.auth.sessionStatus,
        _isLoading,
        _availableSkills,
        _selectedSkillIds
    ) { events, index, assignments, authStatus, isLoading, availableSkills, selectedSkillIds ->
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
            isLoading = isLoading,
            availableSkills = availableSkills,
            selectedSkillIds = selectedSkillIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsState()
    )

    init {
        viewModelScope.launch {
            _currentIndex.collect { index ->
                fetchSkills(index)
            }
        }
    }

    private fun fetchSkills(index: Int) {
        val event = _events.value.getOrNull(index) ?: return
        viewModelScope.launch {
            calendarDataSource.fetchSkillsForEvent(event.id).onSuccess {
                _availableSkills.value = it
                _selectedSkillIds.value = emptySet()
            }
        }
    }

    fun onAction(action: DetailsAction) {
        when (action) {
            DetailsAction.ApplyClicked -> applyForEvent()
            DetailsAction.CancelClicked -> cancelAssignment()
            DetailsAction.NextClicked -> _currentIndex.update { (it + 1).coerceAtMost(_events.value.size - 1) }
            DetailsAction.PrevClicked -> _currentIndex.update { (it - 1).coerceAtLeast(0) }
            is DetailsAction.SkillToggled -> toggleSkill(action.skillId)
        }
    }

    private fun toggleSkill(skillId: Long) {
        _selectedSkillIds.update { 
            if (it.contains(skillId)) it - skillId else it + skillId
        }
    }

    private fun applyForEvent() {
        val eventId = state.value.event?.id ?: return
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return
        
        val skillsString = _availableSkills.value
            .filter { _selectedSkillIds.value.contains(it.id) }
            .joinToString(":") { it.name }
            .takeIf { it.isNotEmpty() }

        viewModelScope.launch {
            _isLoading.value = true
            if (calendarDataSource.applyForEvent(userId, eventId, skillsString)) {
                _effects.emit(DetailsEffect.RegistrationStatusChanged("Application successful"))
            } else {
                _effects.emit(DetailsEffect.RegistrationStatusChanged("Application failed"))
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
                _effects.emit(DetailsEffect.RegistrationStatusChanged("Registration canceled"))
            } else {
                _effects.emit(DetailsEffect.RegistrationStatusChanged("Cancellation failed"))
            }
            _isLoading.value = false
        }
    }
}
