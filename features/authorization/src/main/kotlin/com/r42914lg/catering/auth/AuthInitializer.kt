package com.r42914lg.catering.auth

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AuthInitializer {
    val module = module {
        viewModel { AuthViewModel(get(), get()) }
    }
}
