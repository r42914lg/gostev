plugins {
    alias(libs.plugins.kotlin.library)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    api(libs.koin.core)
}