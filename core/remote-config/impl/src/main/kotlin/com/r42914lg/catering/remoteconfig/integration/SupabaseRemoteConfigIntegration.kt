package com.r42914lg.catering.remoteconfig.integration

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap

internal class SupabaseRemoteConfigIntegration(
    private val supabaseClient: SupabaseClient
) : RemoteConfigIntegration {

    override val isEnabled: Boolean = true
    override var isInitialized: Boolean = false
    override val priority: Int = 1

    private val configMap = ConcurrentHashMap<String, String>()

    override suspend fun fetch(appContext: Context) {
        try {
            val results = supabaseClient.from("remote_config")
                .select()
                .decodeList<RemoteConfigItem>()

            configMap.clear()
            results.forEach { item ->
                configMap[item.key] = item.value
            }
            isInitialized = true
        } catch (_: Exception) {
            /* no-op */
        }
    }

    override fun contains(key: String): Boolean = configMap.containsKey(key)

    override fun getString(key: String): String = configMap[key] ?: ""

    @Serializable
    private data class RemoteConfigItem(
        @SerialName("key") val key: String,
        @SerialName("value") val value: String
    )
}
