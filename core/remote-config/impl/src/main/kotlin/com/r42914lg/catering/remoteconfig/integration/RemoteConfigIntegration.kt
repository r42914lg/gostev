package com.r42914lg.catering.remoteconfig.integration

import android.content.Context

interface RemoteConfigIntegration {
    val isEnabled: Boolean
    var isInitialized: Boolean
    val priority: Int
    suspend fun fetch(appContext: Context)
    fun contains(key: String): Boolean
    fun getString(key: String): String
    fun getAll(): Map<String, String>
}