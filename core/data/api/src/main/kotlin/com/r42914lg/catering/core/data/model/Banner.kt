package com.r42914lg.catering.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: Long = 0,
    val description: String = "",
    @SerialName("image_path")
    val imagePath: String = "",
    @SerialName("event_id")
    val eventId: Long? = null,
    val version: Int = 1
)
