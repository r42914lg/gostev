plugins {
    alias(libs.plugins.compose.library)
}

android {
    namespace = "com.r42914lg.catering.utils"
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
}
