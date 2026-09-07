package com.r42914lg.catering.core.data.model

import kotlinx.serialization.Serializable
@Serializable
data class User(
    val id: Long = 0,
    val name: String = "",
)