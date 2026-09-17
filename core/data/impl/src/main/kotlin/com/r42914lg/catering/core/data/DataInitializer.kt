package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.internal.CalendarDataSourceImpl
import com.r42914lg.catering.core.data.internal.UserDataSourceImpl
import com.r42914lg.catering.core.data.internal.UserManagerImpl
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
        single<CalendarDataSource> { CalendarDataSourceImpl(get()) }
        single<UserDataSource> { UserDataSourceImpl(get()) }
        single<UserManager> { UserManagerImpl(get()) }
    }
}