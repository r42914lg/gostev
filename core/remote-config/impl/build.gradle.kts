import com.r42914lg.catering.plugins.ext.JsonFlatMapParser
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

plugins {
    alias(libs.plugins.android.library)
}

fun getRemoteConfigDefaultString(): String {
    val jsonFile = File(rootDir, "remoteconfig.json")
    val jsonString = jsonFile.readText()
    val jsonObject = JsonFlatMapParser(jsonString).parse()

    val builder = StringBuilder()
    builder.append("new java.util.HashMap<String, String>() {{")
    var paramCount = 0
    for ((key, value) in jsonObject) {
        val escapedValue = value.toString().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
        builder.append(" put(\"${key}\", \"${escapedValue}\");")
        paramCount++
    }
    builder.append(" }}")
    println("Generated HashMap with $paramCount parameters from remoteconfig.json")
    return builder.toString()
}

android {
    namespace = "com.r42914lg.catering.remoteconfig"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "java.util.Map<String, String>",
            "DEFAULT_CONFIG",
            getRemoteConfigDefaultString()
        )
    }
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