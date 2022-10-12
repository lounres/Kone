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
            subdirs("kotlin")
        }
    }
    "utils" {
        "collections"()
        "kotest"("kotlin")
        "mapOperations"()
    }
}

featuresManagement {
    tagRules {
        "kotlin multiplatform" since { hasTags("kotlin") }
        "kotlin common settings" since { hasTags("kotlin jvm", "kotlin multiplatform") }
    }
}