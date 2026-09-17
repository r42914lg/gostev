package com.r42914lg.catering

import com.r42914lg.catering.banners.BannersInitializer
import com.r42914lg.catering.secret.SecretPanelInitializer
import com.r42914lg.catering.mvi.MainViewModel
import com.r42914lg.catering.usecase.ObserveBannersUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AppInitializer {
    val module = module {
        single<CoreApp> { CateringApp() }
        single { ObserveBannersUseCase(get(), get()) }
        viewModel {
            MainViewModel(
                calendarDataSource = get(),
                userManager = get(),
                remoteConfig = get(),
                observeBannersUseCase = get()
            )
        }
        includes(SecretPanelInitializer().module)
        includes(BannersInitializer().module)
    }
}