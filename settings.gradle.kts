rootProject.name = "Kone-project"

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
    id("com.lounres.gradle.feature") version "1.0.0"
}

structuring {
    "docs"()
    "kone" {
        subdirs("kotlin")
        "misc" {
            subdirs("kotlin misc")
        }
    }
    "utils" {
        subdirs("kotlin utility")
    }
}

featuresManagement {
    tagRules {
        "kotlin kone" since { hasAnyOfTags("kotlin", "kotlin misc") }
        "kotlin multiplatform" since { hasAnyOfTags("kotlin kone", "kotlin utility") }
        "kotlin common settings" since { hasAnyOfTags("kotlin jvm", "kotlin multiplatform") }
        "kotest" since { hasTag("kotlin common settings") }
        "publishing" since { hasAnyOfTags("kotlin kone", "kotlin utility") }
        "dokka" since { hasTag("publishing") }
    }
    features {
        on("kotlin") {
            group = "com.lounres.kone"
        }
        on("kotlin misc") {
            group = "com.lounres.kone.misc"
        }
        on("kotlin utility") {
            group = "com.lounres.kone.utils"
        }
    }
}