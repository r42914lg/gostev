package com.r42914lg.catering.remoteconfig.integration

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException

internal class SupabaseRemoteConfigIntegration(
    private val supabaseClient: SupabaseClient,
) : RemoteConfigIntegration {

    override val isEnabled: Boolean = true
    override var isInitialized: Boolean = false
    override val priority: Int = 1

    private val configMap = ConcurrentHashMap<String, String>()

    override suspend fun fetch(appContext: Context) {
        try {
            supabaseClient.auth.sessionStatus.first { it !is SessionStatus.Initializing }

            val results = supabaseClient
                .from("remote_config")
                .select()
                .decodeList<RemoteConfigItem>()

            configMap.clear()
            results.forEach { item ->
                configMap[item.key] = item.value
            }
            isInitialized = true
        } catch (e: CancellationException) {
            throw e
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
        @SerialName("value") val value: String,
    )
}