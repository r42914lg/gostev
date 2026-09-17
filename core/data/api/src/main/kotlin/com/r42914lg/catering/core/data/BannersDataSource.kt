package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.Banner

interface BannersDataSource {
    suspend fun fetchBanners(): Result<List<Banner>>
}
