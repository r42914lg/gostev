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
    private var lastFetchTime: Long = 0
    private val cacheTtlMs = 60 * 60 * 1000

    override suspend fun fetch(appContext: Context) {
        val currentTime = System.currentTimeMillis()
        if (isInitialized && (currentTime - lastFetchTime < cacheTtlMs)) {
            return
        }

        try {
            val results = supabaseClient.from("remote_config")
                .select()
                .decodeList<RemoteConfigItem>()

            configMap.clear()
            results.forEach { item ->
                configMap[item.key] = item.value
            }
            isInitialized = true
            lastFetchTime = currentTime
        } catch (_: Exception) {
            /* no-op */
        }
    }

    override fun contains(key: String): Boolean = configMap.containsKey(key)

    override fun getString(key: String): String = configMap[key] ?: ""

    override fun getAll(): Map<String, String> = configMap.toMap()

    @Serializable
    private data class RemoteConfigItem(
        @SerialName("key") val key: String,
        @SerialName("value") val value: String
    )
}
