import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

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
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.kotlin.link")
        maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
        mavenLocal()
    }
    
    // FIXME: KT-68533
//    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    
    versionCatalogs {
        create("versions").from("dev.lounres:versions:$versions")
    }
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
    }
}

plugins {
    id("dev.lounres.gradle.stal") version "0.4.0"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    kotlin("compiler.plugin.devkit") version "0.0.4-dev-380"
}

stal {
    structure {
        taggedWith("version catalog", "publishing")
        defaultIncludeIf = { directory ->
            !directory.resolve(".ignore.stal").exists() && directory.toPath().listDirectoryEntries().any { it.name != "build" || !it.isDirectory() }
        }
        "libs" {
            "main" {
                subdirs("libs main", "libs public", "libs", "kotlin multiplatform", "kotlin library settings", "publishing", "testBalloon", "dokka") {
                    "algorithms"("libs main algorithms", "libs main extra", "kotlin multiplatform", "kotlin library settings")
                    "assertions"("libs main assertions", "libs main extra", "kotlin multiplatform", "kotlin library settings")
                    "benchmarks"("libs main benchmarks", "libs main extra", "kotlin multiplatform")
                    "examples"("libs main examples", "libs main extra", "kotlin multiplatform")
                }
            }
            "misc" {
                subdirs("libs misc", "libs public", "libs", "kotlin multiplatform", "kotlin library settings", "publishing", "testBalloon", "dokka")
            }
            "util" {
                subdirs("libs util", "libs", "kotlin multiplatform", "kotlin library settings", "publishing", "dokka")
            }
        }
//        "bom"("bom", "kotlin multiplatform", "kotlin library settings", "publishing")
        "plugins" {
            "util"("kotlin compiler library")
            "testUtil"("kotlin compiler library")
            subdirs("kotlin compiler plugin", "publishing") {
                "runtime"("kotlin compiler plugin runtime", "kotlin multiplatform", "kotlin library settings", "publishing", "dokka")
                "gradleWrapper"("kotlin compiler plugin gradle wrapper")
            }
        }
        "docs"()
    }

    tag {
        "kotlin multiplatform publication" since { has("publishing") && has("kotlin multiplatform") }
        "kotlin jvm publication" since { has("publishing") && has("kotlin jvm") }
    }

    action {
        val now: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
        // FIXME
        val koneVersion = "0.0.0-experiment-${now.year}.${now.month.value}.${now.dayOfMonth}.${now.hour}"
        //val koneVersion = "0.0.0-experiment"
        gradle.allprojects {
            group = "${settings.extra["group"]}${project.path.replace(':', '.').let { if (it == ".") ".kone" else ".kone$it" }}"
            extra["koneVersion"] = koneVersion
            extra["koneGroup"] = settings.extra["group"]
            extra["artifactId"] = ""
            extra["alias"] = ""
            extra["androidNamespace"] = ""
            extra["isDokkaConfigured"] = false
            extra["jvmTargetVersion"] = (settings.extra["jvmTargetVersion"] as String).toInt()
            extra["jvmVendor"] = settings.extra["jvmVendor"]
        }
        "libs main" {
            extra["artifactId"] = "kone.${project.name}"
            extra["alias"] = project.name
            extra["androidNamespace"] = "dev.lounres.kone.${project.name}"
        }
        "libs main extra" {
            val parentProject = project.parent!!
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
        "kotlin compiler library" {
            extra["artifactId"] = "kone.plugins.${project.name}"
//            extra["alias"] = "plugins-${project.name}"
        }
        "kotlin compiler plugin" {
            extra["artifactId"] = "kone.plugins.${project.name}"
//            extra["alias"] = "plugins-${project.name}"
        }
        "kotlin compiler plugin runtime" {
            extra["artifactId"] = "kone.plugins.${project.parent!!.name}.runtime"
//            extra["alias"] = "plugins-${project.parent!!.name}-runtime"
            extra["androidNamespace"] = "dev.lounres.kone.plugins.${project.parent!!.name}.runtime"
        }
        "kotlin compiler plugin gradle wrapper" {
            extra["artifactId"] = "kone.plugins.${project.parent!!.name}.gradleWrapper"
            extra["alias"] = "plugins-${project.parent!!.name}"
        }
        "version catalog" {
            extra["artifactId"] = "kone.versionCatalog"
        }
        "libs main benchmarks" {
            extra["jvmTargetVersion"] = (settings.extra["benchmatrksJvmTargetVersion"] as String).toInt()
            extra["jvmVendor"] = settings.extra["benchmatrksJvmVendor"]
        }
        "dokka" {
            extra["isDokkaConfigured"] = true
        }
    }
}