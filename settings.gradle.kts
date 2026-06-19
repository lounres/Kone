rootProject.name = "Kone"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val localProperties = java.util.Properties()
file("local.properties").let { localPropertiesFile ->
    if (localPropertiesFile.exists()) localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

gradle.projectsLoaded {
    for ((key, property) in localProperties) gradle.rootProject.extra[key.toString()] = property
}

val projectProperties = java.util.Properties()
file("gradle.properties").inputStream().use {
    projectProperties.load(it)
}

val versions: String by projectProperties

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://repo.kotlin.link")
        gradlePluginPortal()
        mavenLocal()
    }
    
    versionCatalogs {
        create("versions").from("dev.lounres:versions:$versions")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("dev.lounres.gradle.stal") version "0.4.0"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stal {
    structure {
        taggedWith("publishing", "version catalog")
        defaultIncludeIf = { it.listFiles { file: File -> file.name != "build" || !file.isDirectory }?.isNotEmpty() == true }
        "libs" {
            "main" {
                subdirs("libs main", includeIf = { it.name !in listOf<String>("hooks", "computations", "polynomial") }) { // TODO: Enable the projects eventually
                    "algorithms"("libs main algorithms")
                    "assertions"("libs main assertions")
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
//        "bom"("bom")
        "plugins" {
            subdirs("kotlin compiler plugin") {
                "runtime"("kotlin compiler plugin runtime")
                "testGeneration"("kotlin compiler plugin test generator")
//                "gradleWrapper"()
            }
        }
        "docs"()
    }

    tag {
        // Grouping tags
        "libs main extra" since { hasAnyOf("libs main algorithms", "libs main assertions", "libs main benchmarks", "libs main examples") }
        "libs public" since { hasAnyOf("libs main", "libs misc") }
        "libs" since { hasAnyOf("libs main", "libs misc", "libs util") }
        // Extra structure
        "algorithms" since { has("libs main algorithms") }
        "assertions" since { has("libs main assertions") }
        "benchmarks" since { has("libs main benchmarks") }
        "examples" since { has("libs main examples") }
        // Kotlin set up
        "kotlin multiplatform" since { hasAnyOf("libs", "libs main extra", "bom", "kotlin compiler plugin runtime") }
        "kotlin jvm" since { hasAnyOf("kotlin compiler plugin", "kotlin compiler plugin test generator") }
        "kotlin android" since { has("kotlin multiplatform") && hasAnyOf("libs", "bom") }
        "kotlin common settings" since { hasAnyOf("kotlin multiplatform", "kotlin jvm") }
        "kotlin library settings" since { hasAnyOf("libs", "kotlin compiler plugin runtime", "algorithms", "assertions", "bom") }
        // Extra
        "testBalloon" since { has("libs public") }
//        "kover" since { has("libs public") }
        "kotlin jvm publication" since { hasAnyOf("kotlin compiler plugin") }
        "kotlin multiplatform publication" since { hasAnyOf("libs", "bom", "kotlin compiler plugin runtime") }
        "publishing" since { hasAnyOf("libs", /*"kotlin compiler plugin", "kotlin compiler plugin runtime", "bom"*/) }
        "dokka" since { hasAnyOf("libs", /*"kotlin compiler plugin runtime"*/) }
    }

    action {
        gradle.allprojects {
            extra["artifactId"] = ""
            extra["alias"] = ""
            extra["androidNamespace"] = ""
            extra["isDokkaConfigured"] = false
            extra["jvmTargetVersion"] = settings.extra["jvmTargetVersion"]
            extra["jvmVendor"] = settings.extra["jvmVendor"]
        }
        "libs main" {
            extra["artifactId"] = "kone.${project.name}"
            extra["alias"] = project.name
            extra["androidNamespace"] = "dev.lounres.kone.${project.name}"
        }
        "libs main extra" {
            val parentProject = project.parent!!
            group = "${settings.extra["group"]}${parentProject.path.replace(':', '.')}"
            extra["artifactId"] = "kone.${parentProject.name}.${project.name}"
            extra["alias"] = "${parentProject.name}.${project.name}"
            extra["androidNamespace"] = "dev.lounres.kone.${parentProject.name}.${project.name}"
        }
        "libs misc" {
            extra["artifactId"] = "kone.misc.${project.name}"
            extra["alias"] = "misc-${project.name}"
            extra["androidNamespace"] = "dev.lounres.kone.misc.${project.name}"
        }
        "libs util" {
            extra["artifactId"] = "kone.util.${project.name}"
            extra["alias"] = "util-${project.name}"
            extra["androidNamespace"] = "dev.lounres.kone.util.${project.name}"
        }
        "bom" {
            extra["artifactId"] = "kone.bom"
            extra["alias"] = "bom"
            extra["androidNamespace"] = "dev.lounres.kone.bom"
        }
        "kotlin compiler plugin" {
            extra["artifactId"] = "kone.plugin.${project.name}"
            extra["alias"] = "plugin-${project.name}"
        }
        "kotlin compiler plugin runtime" {
            extra["artifactId"] = "kone.plugin.${project.parent!!.name}.runtime"
            extra["alias"] = "plugin-${project.parent!!.name}-runtime"
            extra["androidNamespace"] = "dev.lounres.kone.plugin.${project.parent!!.name}.runtime"
            extra["jvmTargetVersion"] = settings.extra["pluginRuntimeJvmTargetVersion"]
            extra["jvmVendor"] = settings.extra["pluginRuntimeJvmVendor"]
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