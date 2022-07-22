pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
    }
}

rootProject.name = "Kone"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootDir
    .listFiles { file -> file.isDirectory && file.name.startsWith("kone-", ignoreCase = true) }
    .forEach { include(it.name) }

