package com.r42914lg.catering.core.prefs

import org.koin.dsl.module

class PrefsInitializer {
    val module = module {
        single<PreferencesClient> { PreferencesClientImpl(get()) }
    }
}