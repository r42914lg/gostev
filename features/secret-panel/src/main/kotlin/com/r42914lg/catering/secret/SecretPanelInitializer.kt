package com.r42914lg.catering.secret

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class SecretPanelInitializer {
    val module = module {
        viewModel { SecretPanelViewModel(get()) }
    }
}
