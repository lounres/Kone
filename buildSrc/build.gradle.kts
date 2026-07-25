plugins {
    alias(versions.plugins.kotlin.jvm)/* version "2.4.20-dev-6724"*/
    alias(versions.plugins.kotlinx.serialization)
    `kotlin-dsl`
}

kotlin {
    explicitApiWarning()
    
    sourceSets {
        main {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.versions.kotlin.asProvider().get()}")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:${versions.versions.kotlinx.benchmark.get()}")
                implementation(versions.kotlinx.serialization.core)
//                implementation(versions.kotlinx.serialization.json)
//                implementation(versions.tomlkt)
            }
        }
    }
}