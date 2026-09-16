package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserManager(
    private val supabaseClient: SupabaseClient
) {
    val isAuthenticated = supabaseClient.auth.sessionStatus.map { status ->
        status is SessionStatus.Authenticated
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val userData: Flow<User?> = supabaseClient.auth.sessionStatus.flatMapLatest { status ->
        if (status is SessionStatus.Authenticated) {
            val uid = supabaseClient.auth.currentUserOrNull()?.id
            if (uid != null) {
                flow {
                    try {
                        val user = supabaseClient.from("users")
                            .select { filter { eq("id", uid) } }
                            .decodeSingleOrNull<User>()
                        emit(user)
                    } catch (_: Exception) {
                        emit(null)
                    }
                }
            } else flow { emit(null) }
        } else {
            flow { emit(null) }
        }
    }
}