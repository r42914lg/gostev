package com.r42914lg.catering.core.prefs

import kotlinx.coroutines.flow.Flow

interface PreferencesClient {
    fun isUpgradePending(): Flow<Boolean>
    suspend fun setUpgradePending(forceUpgrade: Boolean)
}