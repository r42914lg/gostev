plugins {
    alias(libs.plugins.android.library)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.r42914lg.catering.remoteconfig"
}

dependencies {
    implementation(project(":core:remote-config:api"))
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)

    api(libs.koin.core)
    api(libs.koin.android)
}
