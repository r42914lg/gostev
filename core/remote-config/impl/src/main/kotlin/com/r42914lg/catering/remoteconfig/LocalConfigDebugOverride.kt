package com.r42914lg.catering.remoteconfig

import java.util.concurrent.ConcurrentHashMap

internal class LocalConfigDebugOverride {
    private val overrides = ConcurrentHashMap<String, String>()
    private val baseConfig = ConcurrentHashMap<String, String>()

    fun getOverride(configKey: RemoteConfigKey): String? = overrides[configKey.key]

    fun setOverride(configKey: RemoteConfigKey, value: String) {
        overrides[configKey.key] = value
    }

    fun clearOverride(configKey: RemoteConfigKey) {
        overrides.remove(configKey.key)
    }

    fun clearAll() {
        overrides.clear()
    }

    fun syncWithBaseConfig(config: Map<String, String>) {
        baseConfig.clear()
        baseConfig.putAll(config)
    }

    fun getEffectiveConfig(): Map<String, String> {
        val result = baseConfig.toMutableMap()
        result.putAll(overrides)
        return result
    }
}
