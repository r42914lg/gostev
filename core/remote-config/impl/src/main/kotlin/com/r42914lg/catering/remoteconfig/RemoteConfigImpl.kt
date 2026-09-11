package com.r42914lg.catering.remoteconfig

import android.content.Context
import com.r42914lg.catering.remoteconfig.integration.RemoteConfigIntegration
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class RemoteConfigImpl(
    private val integration: RemoteConfigIntegration,
    private val debugOverride: LocalConfigDebugOverride,
    private val appContext: Context,
) : RemoteConfig {
    private val defaultMap = RemoteDefaultConfig.DEFAULT_CONFIG_MAP
    private val _updates = MutableSharedFlow<RemoteConfigKey>(extraBufferCapacity = 1)
    override val updates: Flow<RemoteConfigKey> = _updates.asSharedFlow()

    override suspend fun fetch() {
        coroutineScope {
            async { integration.fetch(appContext) }
        }.await()
        syncDebugOverride()
        RemoteConfigKey.entries.forEach { _updates.tryEmit(it) }
    }

    private fun syncDebugOverride() {
        val base = if (isAnyRemoteAvailable()) {
            integration.getAll()
        } else {
            defaultMap.mapValues { it.value.toString() }
        }
        debugOverride.syncWithBaseConfig(base)
    }

    override fun getString(configKey: RemoteConfigKey): String {
        debugOverride.getOverride(configKey)?.let { return it }

        return if (isAnyRemoteAvailable()) {
            integration.getString(configKey.key)
        } else {
            defaultMap[configKey.key]?.toString() ?: ""
        }
    }

    override fun isAnyRemoteAvailable(): Boolean =
        integration.isEnabled && integration.isInitialized

    override fun getDebugInfo(): List<RemoteConfigDebugInfo> {
        return RemoteConfigKey.entries.map { key ->
            val overrideValue = debugOverride.getOverride(key)
            val currentValue = getString(key)
            RemoteConfigDebugInfo(
                configKey = key,
                currentValue = currentValue,
                isOverridden = overrideValue != null
            )
        }
    }

    override fun setDebugOverride(configKey: RemoteConfigKey, value: String) {
        debugOverride.setOverride(configKey, value)
        _updates.tryEmit(configKey)
    }

    override fun clearDebugOverride(configKey: RemoteConfigKey) {
        debugOverride.clearOverride(configKey)
        _updates.tryEmit(configKey)
    }
}