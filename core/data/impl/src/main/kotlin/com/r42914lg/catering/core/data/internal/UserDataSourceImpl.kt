package com.r42914lg.catering.core.data.internal

import com.r42914lg.catering.core.data.UserDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class UserDataSourceImpl(private val supabaseClient: SupabaseClient) : UserDataSource {
    override suspend fun signUp(email: String, password: String, name: String) {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject { put("name", name) }
        }
    }

    override suspend fun signIn(email: String, password: String) {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }
}
