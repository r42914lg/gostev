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
    val userName = supabaseClient.auth.sessionStatus.map { status ->
        if (status is SessionStatus.Authenticated) {
            fetchMyName()
        } else {
            null
        }
    }

    private suspend fun fetchMyName(): String? {
        val uid = supabaseClient.auth.currentUserOrNull()?.id ?: return null

        val user = supabaseClient.from("users")
            .select { filter { eq("id", uid) } }
            .decodeSingleOrNull<User>()

        return user?.name
    }
}