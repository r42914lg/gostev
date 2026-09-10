package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.map

class UserManager(
    private val supabaseClient: SupabaseClient
) {
    val isAuthenticated = supabaseClient.auth.sessionStatus.map { status ->
        status is SessionStatus.Authenticated
    }
}