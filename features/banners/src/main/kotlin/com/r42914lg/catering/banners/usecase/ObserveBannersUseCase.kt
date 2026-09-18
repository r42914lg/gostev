package com.r42914lg.catering.banners.usecase

import com.r42914lg.catering.core.data.BannersDataSource
import com.r42914lg.catering.core.data.model.Banner
import com.r42914lg.catering.remoteconfig.RemoteConfig
import com.r42914lg.catering.remoteconfig.RemoteConfigKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.Duration.Companion.minutes

class ObserveBannersUseCase(
    private val bannersDataSource: BannersDataSource,
    private val remoteConfig: RemoteConfig,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Pair<List<Banner>, String>> =
        remoteConfig.updates
            .filter { it == RemoteConfigKey.RC_BANNERS_VERSION }
            .onStart { emit(RemoteConfigKey.RC_BANNERS_VERSION) }
            .transformLatest {
                while (true) {
                    val banners = loadBanners()
                    val url = remoteConfig.getString(RemoteConfigKey.RC_BANNERS_BUCKET_URL)
                    emit(banners to url)
                    delay(BANNERS_TTL)
                }
            }

    private suspend fun loadBanners(): List<Banner> {
        val version = remoteConfig
            .getString(RemoteConfigKey.RC_BANNERS_VERSION)
            .toIntOrNull()
            ?: 0

        return bannersDataSource.fetchBanners()
            .getOrDefault(emptyList())
            .filter { it.version == version && it.eventId != null }
    }

    private companion object {
        private val BANNERS_TTL = 5.minutes
    }
}
