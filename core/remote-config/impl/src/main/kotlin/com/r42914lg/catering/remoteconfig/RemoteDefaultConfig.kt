package com.r42914lg.catering.remoteconfig

object RemoteDefaultConfig {
    val DEFAULT_CONFIG_MAP: Map<String, Any> = RemoteConfigKey.entries.associate {
        it.key to it.default
    }
}