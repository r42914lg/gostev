plugins {
    alias(libs.plugins.kotlin.library)
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlin.serialization.json)
    api(libs.kotlinx.datetime)
}
