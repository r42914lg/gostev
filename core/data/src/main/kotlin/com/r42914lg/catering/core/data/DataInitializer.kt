package com.r42914lg.catering.core.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

class DataInitializer {
    val module = module {
        single {
            createSupabaseClient(
                supabaseUrl = SupabaseConfig.URL,
                supabaseKey = SupabaseConfig.KEY
            ) {
                install(Postgrest)
                install(Auth)
            }
        }
        single { CalendarDataSource(get()) }
        single { UserManager(get()) }
    }
}