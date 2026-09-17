package com.r42914lg.catering.usecase

import com.r42914lg.catering.core.data.BannersDataSource
import com.r42914lg.catering.core.data.model.Banner
import com.r42914lg.catering.remoteconfig.RemoteConfig
import com.r42914lg.catering.remoteconfig.RemoteConfigKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration.Companion.minutes

internal class ObserveBannersUseCase(
    private val bannersDataSource: BannersDataSource,
    private val remoteConfig: RemoteConfig,
) {
    operator fun invoke(): Flow<List<Banner>> = merge(
        remoteConfig.updates.filter { it == RemoteConfigKey.RC_BANNERS_VERSION },
            flow {
                while (true) {
                    delay(REFRESH_INTERVAL)
                    emit(RemoteConfigKey.RC_BANNERS_VERSION)
                }
            }
        )
        .onStart { emit(RemoteConfigKey.RC_BANNERS_VERSION) }
        .map {
            val version = remoteConfig.getString(RemoteConfigKey.RC_BANNERS_VERSION).toIntOrNull() ?: 0
            bannersDataSource.fetchBanners()
                .getOrDefault(emptyList())
                .filter { it.version == version && it.eventId != null }
        }

    companion object {
        private val REFRESH_INTERVAL = 30.minutes
    }
}
