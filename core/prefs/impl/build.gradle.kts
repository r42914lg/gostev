plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.r42914lg.catering.core.prefs.impl"
}

dependencies {
    implementation(project(":core:prefs:api"))
    implementation(libs.androidx.datastore.preferences)
    api(libs.koin.core)
}