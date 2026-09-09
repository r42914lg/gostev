package com.r42914lg.catering.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class EventAssignment(
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("event_id")
    val eventId: Long = 0,
    val status: Status = Status.APPLIED,
) {
    @Serializable
    enum class Status { 
        @SerialName("APPLIED") APPLIED, 
        @SerialName("CONFIRMED") CONFIRMED 
    }
}