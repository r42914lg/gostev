plugins {
    alias(libs.plugins.kotlin.library)
}

val generateSupabaseConfig by tasks.registering {
    val url = project.findProperty("supabase.url") ?: ""
    val key = project.findProperty("supabase.key") ?: ""
    val outputDir = layout.buildDirectory.dir("generated/supabase/kotlin")

    inputs.property("url", url)
    inputs.property("key", key)
    outputs.dir(outputDir)

    doLast {
        val configFile = outputDir.get().file("com/r42914lg/catering/core/data/SupabaseConfig.kt").asFile
        configFile.parentFile.mkdirs()
        configFile.writeText("""
            package com.r42914lg.catering.core.data

            object SupabaseConfig {
                const val URL = "$url"
                const val KEY = "$key"
            }
        """.trimIndent())
    }
}

kotlin {
    sourceSets.main {
        kotlin.srcDir(generateSupabaseConfig)
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlinx.datetime)
    api(libs.koin.core)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
}