package com.r42914lg.catering.remoteconfig

import com.r42914lg.catering.remoteconfig.integration.RemoteConfigIntegration
import com.r42914lg.catering.remoteconfig.integration.SupabaseRemoteConfigIntegration
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class RemoteConfigInitializer {
    val module = module {
        single { LocalConfigDebugOverride() }
        single<RemoteConfigIntegration> {
            SupabaseRemoteConfigIntegration(get())
        }
        single<RemoteConfig> {
            RemoteConfigImpl(
                get(),
                get(),
                androidContext()
            )
        }
    }
}