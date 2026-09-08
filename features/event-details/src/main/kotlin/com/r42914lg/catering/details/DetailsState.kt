package com.r42914lg.catering.details

import androidx.compose.runtime.Stable
import com.r42914lg.catering.core.data.model.CalendarEvent

data class DetailsState(
    val event: CalendarEvent? = null,
    val index: Int = 0,
    val total: Int = 0,
    val status: Status = Status.NOT_REGISTERED,
    val isAuthorized: Boolean = false,
    val isLoading: Boolean = false
) {
    enum class Status { NOT_REGISTERED, APPLIED, CONFIRMED }
}

@Stable
sealed interface DetailsAction {
    data object ApplyClicked : DetailsAction
    data object CancelClicked : DetailsAction
    data object NextClicked : DetailsAction
    data object PrevClicked : DetailsAction
}
