plugins {
    alias(libs.plugins.compose.library)
}

android {
    namespace = "com.r42914lg.catering.details"
}

dependencies {
    implementation(project(":core:data:api"))
    implementation(project(":platform:utils"))
    implementation(project(":platform:android-utils"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}