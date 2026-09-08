package com.r42914lg.catering.core.prefs

import kotlinx.coroutines.flow.Flow

interface PreferencesClient {
    fun isUpdatePending(): Flow<Boolean>
    suspend fun setUpdatePending(forceUpgrade: Boolean)
}