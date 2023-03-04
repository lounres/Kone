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
    id("com.lounres.gradle.feature") version "1.1.0"
}

structuring {
    "docs"()
    "libs" {
        "main" {
            subdirs("libs main")
        }
        "misc" {
            subdirs("libs misc")
        }
        "util" {
            subdirs("libs util")
        }
    }
}

featuresManagement {
    tagsAssignment {
        // Grouping tags
        "libs public" since { hasAnyOfTags("libs main", "libs misc") }
        "libs" since { hasAnyOfTags("libs main", "libs misc", "libs util") }
        // Kotlin set up
        "kotlin multiplatform" since { hasAnyOfTags("libs") }
        "kotlin common settings" since { hasAnyOfTags("kotlin multiplatform", "kotlin jvm") }
        "kotlin library settings" since { hasTag("libs") }
        // Extra
        "examples" since { hasTag("libs public") }
        "benchmark" since { hasTag("libs public") }
        "kotest" since { hasTag("libs public") }
        "publishing" since { hasAnyOfTags("libs") }
        "dokka" since { hasTag("publishing") }
        "versionCatalog bundle main" since  { hasAllOfTags("publishing", "libs main") }
        "versionCatalog bundle misc" since  { hasAllOfTags("publishing", "libs misc") }
        "versionCatalog bundle util" since  { hasAllOfTags("publishing", "libs util") }
    }
    features {
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
        fun addBundleCollectingAction(tag: String) {
            on("versionCatalog bundle $tag") {
                (rootProject.extra["bundle $tag aliases"] as MutableList<String>).add("${extra["aliasPrefix"]}${project.name}")
            }
        }
        addBundleCollectingAction("main")
        addBundleCollectingAction("misc")
        addBundleCollectingAction("util")
    }
}