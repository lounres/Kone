rootProject.name = "project1"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
        maven {
            url = uri(System.getProperty("com.autonomousapps.plugin-under-test.repo", "").replace("\\", "\\\\"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
        maven {
            url = uri(System.getProperty("com.autonomousapps.plugin-under-test.repo", "").replace("\\", "\\\\"))
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}