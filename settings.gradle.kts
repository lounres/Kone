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
    id("com.lounres.gradle.stal") version "1.0.0"
}

stal {
    structure {
        "docs"()
        "libs" {
            "main" {
                "core"("libs main")
                subdirs("libs main", "uses libs main core")
                "geometry"(/*"kotlin multiplatform"*/)
            }
            "misc" {
                subdirs("libs misc")
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
//        "dokka" since { has("publishing") }
        "versionCatalog bundle main" since { hasAllOf("publishing", "libs main") }
        "versionCatalog bundle misc" since { hasAllOf("publishing", "libs misc") }
        "versionCatalog bundle util" since { hasAllOf("publishing", "libs util") }
    }

    action {
        gradle.allprojects {
            extra["artifactPrefix"] = ""
            extra["aliasPrefix"] = ""
        }
        on("libs main") {
            extra["artifactPrefix"] = "kone."
            extra["aliasPrefix"] = ""
        }
        on("libs misc") {
            extra["artifactPrefix"] = "kone.misc."
            extra["aliasPrefix"] = "misc-"
        }
        on("libs util") {
            extra["artifactPrefix"] = "kone.util."
            extra["aliasPrefix"] = "util-"
        }
    }
}