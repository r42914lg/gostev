package com.r42914lg.catering

import com.r42914lg.catering.banners.BannersInitializer
import com.r42914lg.catering.secret.SecretPanelInitializer
import com.r42914lg.catering.mvi.MainViewModel
import com.r42914lg.catering.usecase.GetAllEventsUseCase
import com.r42914lg.catering.usecase.ObserveCalendarUseCase
import com.r42914lg.catering.usecase.ObserveForceUpdateUseCase
import com.r42914lg.catering.usecase.RefreshCalendarDataUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AppInitializer {
    val module = module {
        single<CoreApp> { CateringApp() }
        single { ObserveForceUpdateUseCase(get()) }
        single { ObserveCalendarUseCase(get()) }
        single { RefreshCalendarDataUseCase(get()) }
        single { GetAllEventsUseCase(get()) }
        viewModel {
            MainViewModel(
                userManager = get(),
                observeBannersUseCase = get(),
                observeForceUpdateUseCase = get(),
                observeCalendarUseCase = get(),
                refreshCalendarDataUseCase = get(),
                getAllEventsUseCase = get()
            )
        }
        includes(SecretPanelInitializer().module)
        includes(BannersInitializer().module)
    }
}
