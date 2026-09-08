package com.r42914lg.catering.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PreferencesClientImpl(
    private val context: Context
) : PreferencesClient {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun isUpdatePending(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_BLOCK_UNTIL_UPGRADED] ?: false
    }

    override suspend fun setUpdatePending(forceUpgrade: Boolean) {
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[KEY_BLOCK_UNTIL_UPGRADED] = forceUpgrade
            }
        }
    }

    companion object {
        val KEY_BLOCK_UNTIL_UPGRADED = booleanPreferencesKey("block_until_upgraded")
    }
}