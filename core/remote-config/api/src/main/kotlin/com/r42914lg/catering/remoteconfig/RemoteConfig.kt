package com.r42914lg.catering.remoteconfig

import kotlinx.coroutines.flow.Flow

interface RemoteConfig {
    val updates: Flow<RemoteConfigKey>
    fun isAnyRemoteAvailable(): Boolean
    suspend fun fetch()
    fun getString(configKey: RemoteConfigKey): String

    fun getDebugInfo(): List<RemoteConfigDebugInfo>
    fun setDebugOverride(configKey: RemoteConfigKey, value: String)
    fun clearDebugOverride(configKey: RemoteConfigKey)
}

data class RemoteConfigDebugInfo(
    val configKey: RemoteConfigKey,
    val currentValue: String,
    val isOverridden: Boolean
)