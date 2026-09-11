plugins {
    alias(libs.plugins.android.library)
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

    api(libs.koin.core)
    api(libs.koin.android)
}
