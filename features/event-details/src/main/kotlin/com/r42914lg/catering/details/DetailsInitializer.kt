package com.r42914lg.catering.details

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class DetailsInitializer {
    val module = module {
        viewModel { params ->
            DetailsViewModel(
                initialEvents = params.get(),
                calendarDataSource = get(),
                supabaseClient = get()
            )
        }
    }
}
