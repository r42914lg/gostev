package com.r42914lg.catering

import com.r42914lg.catering.ui.mvi.MainStateHolder
import com.r42914lg.catering.ui.mvi.MainViewModel
import org.koin.dsl.module

class AppInitializer {
    val module = module {
        single<CoreApp> { CateringApp() }
        factory<MainStateHolder> {
            MainViewModel(
                get(),
            )
        }
    }
}