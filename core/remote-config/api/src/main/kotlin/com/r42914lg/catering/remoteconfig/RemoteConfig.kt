package com.r42914lg.catering.remoteconfig

interface RemoteConfig {
    fun isAnyRemoteAvailable(): Boolean
    suspend fun fetch()
    fun getString(configKey: RemoteConfigKey): String
}