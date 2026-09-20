package com.r42914lg.catering.usecase

import com.r42914lg.catering.BuildConfig
import com.r42914lg.catering.remoteconfig.RemoteConfig
import com.r42914lg.catering.remoteconfig.RemoteConfigKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal class ObserveForceUpdateUseCase(
    private val remoteConfig: RemoteConfig
) {
    operator fun invoke(): Flow<Boolean> = remoteConfig.updates
        .filter { it == RemoteConfigKey.RC_MIN_VERSION }
        .onStart { emit(RemoteConfigKey.RC_MIN_VERSION) }
        .map {
            val minVersion = remoteConfig.getString(RemoteConfigKey.RC_MIN_VERSION).toIntOrNull() ?: 0
            minVersion > BuildConfig.VERSION_CODE
        }
        .distinctUntilChanged()
}
