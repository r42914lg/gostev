package com.r42914lg.catering.core.data.internal

import com.r42914lg.catering.core.data.BannersDataSource
import com.r42914lg.catering.core.data.model.Banner
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

internal class BannersDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : BannersDataSource {
    override suspend fun fetchBanners(): Result<List<Banner>> = try {
        Result.success(supabaseClient.from("banners").select().decodeList())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
