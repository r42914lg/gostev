package com.r42914lg.catering.core.data

interface UserDataSource {
    suspend fun signUp(email: String, password: String, name: String)
    suspend fun signIn(email: String, password: String)
}
