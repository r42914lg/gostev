package com.r42914lg.catering.remoteconfig

import android.content.Context
import com.r42914lg.catering.remoteconfig.integration.RemoteConfigIntegration
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class RemoteConfigImpl(
    private val integration: RemoteConfigIntegration,
    private val appContext: Context,
) : RemoteConfig {
    private val defaultMap = RemoteDefaultConfig.DEFAULT_CONFIG_MAP

    override suspend fun fetch() = coroutineScope {
        async { integration.fetch(appContext) }
    }.await()

    override fun getString(configKey: RemoteConfigKey): String =
        if (isAnyRemoteAvailable()) {
            integration.getString(configKey.key)
        } else {
            defaultMap[configKey.key] as? String ?: ""
        }

    override fun isAnyRemoteAvailable(): Boolean =
        integration.isEnabled && integration.isInitialized
}