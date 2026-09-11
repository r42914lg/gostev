package com.r42914lg.catering

import com.r42914lg.catering.mvi.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AppInitializer {
    val module = module {
        single<CoreApp> { CateringApp() }
        viewModel {
            MainViewModel(
                calendarDataSource = get(),
                userManager = get(),
                remoteConfig = get(),
            )
        }
    }
}