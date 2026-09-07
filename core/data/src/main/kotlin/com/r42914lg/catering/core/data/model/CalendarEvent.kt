package com.r42914lg.catering.core.data.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
@Serializable
data class CalendarEvent(
    val id: Long = 0,
    val date: LocalDate,
    val name: String = "",
    val isAvailable: Boolean = false,
)