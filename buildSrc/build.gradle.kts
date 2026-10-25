import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(versions.plugins.kotlin.jvm)
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

tasks.withType<KotlinCompile> {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_3
        languageVersion = KotlinVersion.KOTLIN_2_3
    }
}