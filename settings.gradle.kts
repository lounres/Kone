rootProject.name = "Kone"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    id("dev.lounres.gradle.stal") version "0.3.1"
}

stal {
    structure {
        defaultIncludeIf = { it.listFiles { file: File -> file.name != "build" || !file.isDirectory }?.isNotEmpty() ?: false }
        "libs" {
            "main" {
                "core"("libs main")
                subdirs("libs main", "uses libs main core")
            }
            "misc" {
                subdirs("libs misc", "uses libs main core")
            }
            "util" {
                subdirs("libs util")
            }
        }
    }

    tag {
        // Grouping tags
        "libs public" since { hasAnyOf("libs main", "libs misc") }
        "libs" since { hasAnyOf("libs main", "libs misc", "libs util") }
        // Kotlin set up
        "kotlin multiplatform" since { hasAnyOf("libs") }
        "kotlin common settings" since { hasAnyOf("kotlin multiplatform", "kotlin jvm") }
        "kotlin library settings" since { has("libs") }
        // Extra
        "examples" since { has("libs public") }
        "benchmark" since { has("libs public") }
        "kotest" since { has("libs public") }
        "publishing" since { hasAnyOf("libs") }
        "dokka" since { has("libs") }
        "versionCatalog bundle main" since { hasAllOf("publishing", "libs main") }
        "versionCatalog bundle misc" since { hasAllOf("publishing", "libs misc") }
        "versionCatalog bundle util" since { hasAllOf("publishing", "libs util") }
    }

    action {
        gradle.allprojects {
            extra["artifactPrefix"] = ""
            extra["aliasPrefix"] = ""
        }
        "libs main" {
            extra["artifactPrefix"] = "kone."
            extra["aliasPrefix"] = ""
        }
        "libs misc" {
            extra["artifactPrefix"] = "kone.misc."
            extra["aliasPrefix"] = "misc-"
        }
        "libs util" {
            extra["artifactPrefix"] = "kone.util."
            extra["aliasPrefix"] = "util-"
        }
    }
}