package com.r42914lg.catering.core.data

import org.koin.dsl.module

class DataInitializer {
    val module = module {
        single { CalendarDataSource() }
    }
}