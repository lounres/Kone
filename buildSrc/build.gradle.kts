plugins {
    alias(versions.plugins.kotlin.jvm)
    alias(versions.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
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