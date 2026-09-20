package com.r42914lg.catering.mvi

import com.r42914lg.catering.core.data.model.Banner
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import kotlinx.datetime.LocalDate

internal data class ScreenState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val monthTitle: String = "",
    val yearTitle: String = "",
    val days: List<CalendarDay> = emptyList(),
    val assignments: List<EventAssignment> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val bannersBaseUrl: String = "",
    val isUpdateRequired: Boolean = false,
) {
    val hasBanners: Boolean
        get() = banners.isNotEmpty() && bannersBaseUrl.isNotBlank()
}

internal data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val events: List<CalendarEvent> = emptyList(),
    val dots: List<EventDot> = emptyList()
)

internal data class EventDot(
    val status: Status,
    val eventId: Long,
) {
    enum class Status { NOT_REGISTERED, APPLIED, CONFIRMED }
}