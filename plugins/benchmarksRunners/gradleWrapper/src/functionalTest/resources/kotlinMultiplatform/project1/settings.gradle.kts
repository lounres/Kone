rootProject.name = "project1"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri(System.getProperty("com.autonomousapps.plugin-under-test.repo", "").replace("\\", "\\\\"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}