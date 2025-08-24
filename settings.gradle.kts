rootProject.name = "Kone"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val projectProperties = java.util.Properties()
file("gradle.properties").inputStream().use {
    projectProperties.load(it)
}

val versions: String by projectProperties

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
        mavenLocal()
    }
    
    versionCatalogs {
        create("versions").from("dev.lounres:versions:$versions")
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.lounres.gradle.stal") version "0.4.0"
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.10.0")
}

stal {
    structure {
        taggedWith("publishing", "version catalog")
        defaultIncludeIf = { it.listFiles { file: File -> file.name != "build" || !file.isDirectory }?.isNotEmpty() == true }
        "libs" {
            "main" {
                subdirs("libs main", includeIf = { it.name !in listOf<String>("graphs", "hooks", "computations") }) { // TODO: Enable the projects eventually
                    "algorithms"("libs main algorithms")
                    "benchmarks"("libs main benchmarks")
                    "examples"("libs main examples")
                }
            }
            "misc" {
                subdirs("libs misc", includeIf = { it.name !in listOf("lattices", "planimetricsCalculus") }) // TODO: Enable the projects eventually
            }
            "util" {
                subdirs("libs util")
            }
        }
        "plugins" {
            subdirs("kotlin compiler plugin") {
                "testGeneration"("kotlin compiler plugin test generator")
//                "gradleWrapper"()
            }
        }
        "docs"()
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
        "kotlin jvm" since { hasAnyOf("kotlin compiler plugin", "kotlin compiler plugin test generator") }
        "kotlin common settings" since { hasAnyOf("kotlin multiplatform", "kotlin jvm") }
        "kotlin library settings" since { hasAnyOf("libs", "algorithms") }
        // Extra
        "kotest" since { has("libs public") }
        "kover" since { has("libs public") }
        "kotlin jvm publication" since { hasAnyOf("kotlin compiler plugin") }
        "kotlin multiplatform publication" since { hasAnyOf("libs") }
        "publishing" since { has("libs") }
        "dokka" since { has("libs") }
    }

    action {
        gradle.allprojects {
            extra["artifactId"] = ""
            extra["alias"] = ""
            extra["isDokkaConfigured"] = false
            extra["jvmTargetVersion"] = settings.extra["jvmTargetVersion"]
            extra["jvmVendor"] = settings.extra["jvmVendor"]
        }
        "libs main" {
            extra["artifactId"] = "kone.${project.name}"
            extra["alias"] = project.name
        }
        "libs misc" {
            extra["artifactId"] = "kone.misc.${project.name}"
            extra["alias"] = "misc-${project.name}"
        }
        "libs util" {
            extra["artifactId"] = "kone.util.${project.name}"
            extra["alias"] = "util-${project.name}"
        }
        "plugin" {
            extra["artifactId"] = "kone.plugin.${project.name}"
            extra["alias"] = "plugin-${project.name}"
        }
        "version catalog" {
            extra["artifactId"] = "kone.versionCatalog"
        }
        "benchmarks" {
            extra["jvmTargetVersion"] = settings.extra["benchmatrksJvmTargetVersion"]
            extra["jvmVendor"] = settings.extra["benchmatrksJvmVendor"]
        }
        "dokka" {
            extra["isDokkaConfigured"] = true
        }
    }
}