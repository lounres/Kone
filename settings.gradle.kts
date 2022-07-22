pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    val kotlinVersion: String by settings
    val dokkaVersion: String by settings
    val kotestVersion: String by settings
    val koverVersion: String by settings
    val benchmarkVersion: String by settings
    val abiCompValidatorVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion

        id("org.jetbrains.dokka") version dokkaVersion
        id("io.kotest.multiplatform") version kotestVersion
        id("org.jetbrains.kotlinx.kover") version koverVersion
        id("org.jetbrains.kotlinx.benchmark") version benchmarkVersion
        id("org.jetbrains.kotlinx.binary-compatibility-validator") version abiCompValidatorVersion
    }
}

rootProject.name = "Kone"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootDir
    .listFiles { file -> file.isDirectory && file.name.startsWith("kone-", ignoreCase = true) }
    .forEach { include(it.name) }

