package com.r42914lg.catering.core.data.model

import kotlinx.serialization.Serializable
@Serializable
data class EventAssignment(
    val userId: String = "",
    val eventId: Long = 0,
    val status: Status = Status.APPLIED,
) {
    enum class Status { APPLIED, CONFIRMED }
}