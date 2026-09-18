package com.r42914lg.catering.remoteconfig

enum class RemoteConfigKey(val key: String, val default: Any) {
    RC_MIN_VERSION("min_app_version", "1"),
    RC_BANNERS_VERSION("banners_version", "0"),
    RC_BANNERS_BUCKET_URL("banners_bucket_url", "");
}
