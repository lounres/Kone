@file:Suppress("UnstableApiUsage")

rootProject.name = "buildSrc"

val projectProperties = java.util.Properties()
file("../gradle.properties").inputStream().use {
    projectProperties.load(it)
}

val versions: String by projectProperties

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
        maven("https://repo.kotlin.link")
        mavenLocal()
    }
    
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    
    versionCatalogs {
        create("versions").from("dev.lounres:versions:$versions")
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    }
}