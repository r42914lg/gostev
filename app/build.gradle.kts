plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.r42914lg.catering"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.r42914lg.catering"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:data:api"))
    implementation(project(":core:data:impl"))
    implementation(project(":platform:android-utils"))
    implementation(project(":platform:design-sys"))
    implementation(project(":platform:utils"))
    implementation(project(":core:remote-config:api"))
    implementation(project(":core:prefs:api"))
    implementation(project(":core:remote-config:impl"))
    implementation(project(":core:prefs:impl"))
    implementation(project(":features:event-details"))
    implementation(project(":features:authorization"))
    implementation(project(":features:secret-panel"))
    implementation(project(":features:banners"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}