@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    `kotlin-dsl`
    `version-catalog`
}

java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.kotlin.link")
}