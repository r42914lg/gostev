plugins {
    alias(libs.plugins.compose.library)
}

android {
    namespace = "com.r42914lg.catering.auth"
}

dependencies {
    implementation(project(":core:data"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
}