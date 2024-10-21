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
        mavenCentral()
//        mavenLocal()
    }
}

plugins {
    id("dev.lounres.gradle.stal") version "0.3.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

stal {
    structure {
        defaultIncludeIf = { it.listFiles { file: File -> file.name != "build" || !file.isDirectory }?.isNotEmpty() == true }
        "libs" {
            "main" {
                "core"("libs main") {
                    "benchmarks"("libs main benchmarks")
                    "examples"("libs main examples")
                }
                subdirs("libs main", "libs non-core main", "uses libs main core") {
                    "algorithms"("libs main algorithms")
                    "benchmarks"("libs main benchmarks")
                    "examples"("libs main examples")
                }
            }
            "misc" {
                subdirs("libs misc", "uses libs main core")
            }
            "util" {
                "kotest"()
                subdirs("libs util")
            }
        }
        "test"("kotlin multiplatform")
    }

    tag {
        // Grouping tags
        "libs main extra" since { hasAnyOf("libs main algorithms", "libs main benchmarks", "libs main examples") }
        "libs public" since { hasAnyOf("libs main", "libs misc") }
        "libs" since { hasAnyOf("libs main", "libs misc", "libs util") }
        // Extra structure
        "algorithms" since { has("libs main algorithms") }
        "benchmarks" since { has("libs main benchmarks") }
        "examples" since { has("libs main examples") }
        // Kotlin set up
        "kotlin multiplatform" since { hasAnyOf("libs", "libs main extra") }
        "kotlin common settings" since { hasAnyOf("kotlin multiplatform", "kotlin jvm") }
        "kotlin library settings" since { hasAnyOf("libs", "algorithms") }
        // Extra
        "kotest" since { has("libs public") }
        "kover" since { has("libs public") }
        "publishing" since { hasAnyOf("libs") }
//        "dokka" since { has("libs") }
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