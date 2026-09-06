package com.r42914lg.nagore

import com.r42914lg.catering.CoreApp
import com.r42914lg.catering.NagoreApp
import com.r42914lg.catering.ui.mvi.MainStateHolder
import com.r42914lg.catering.ui.mvi.MainViewModel
import org.koin.dsl.module

class AppInitializer {
    val module = module {
        single<CoreApp> { NagoreApp() }
        factory<MainStateHolder> {
            MainViewModel(
                get(),
            )
        }
    }
}