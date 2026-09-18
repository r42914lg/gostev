package com.r42914lg.catering.banners

import com.r42914lg.catering.banners.usecase.ObserveBannersUseCase
import org.koin.dsl.module

class BannersInitializer {
    val module = module {
        single { ObserveBannersUseCase(bannersDataSource = get(), remoteConfig = get()) }
    }
}
