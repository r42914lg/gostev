plugins {
    alias(libs.plugins.compose.library)
}

android {
    namespace = "com.r42914lg.catering.designsys"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
}
