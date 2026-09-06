package com.r42914lg.catering.remoteconfig

import com.r42914lg.catering.remoteconfig.integration.RemoteConfigIntegration
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class RemoteConfigInitializer {
    val module = module {
        single<RemoteConfig> {
            RemoteConfigImpl(
                get(),
                androidContext()
            )
        }
    }
}