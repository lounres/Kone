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
        "libs public" since { hasAnyOfTags("libs main", "libs misc") }
        "libs" since { hasAnyOfTags("libs main", "libs misc", "libs util") }
        "kotlin multiplatform" since { hasAnyOfTags("libs") }
        "kotlin common settings" since { hasAnyOfTags("kotlin jvm", "kotlin multiplatform") }
        "kotest" since { hasTag("libs") }
        "publishing" since { hasAnyOfTags("libs") }
        "dokka" since { hasTag("publishing") }
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
    }
}