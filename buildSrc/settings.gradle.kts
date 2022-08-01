@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")


dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}