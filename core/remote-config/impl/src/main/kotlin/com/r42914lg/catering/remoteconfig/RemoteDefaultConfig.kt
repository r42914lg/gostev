package com.r42914lg.catering.remoteconfig

object RemoteDefaultConfig {

    val DEFAULT_STRING_CONFIG_MAP = BuildConfig.DEFAULT_CONFIG

    val DEFAULT_CONFIG_MAP = RemoteConfigKey.values().associate {
        it.toPair()
    }

    private fun RemoteConfigKey.toPair(): Pair<String, Any> {
        val value = when (default) {
            is String -> DEFAULT_STRING_CONFIG_MAP[key] ?: default
            else -> DEFAULT_STRING_CONFIG_MAP[key] ?: default.toString()
        }
        return key to value
    }
}