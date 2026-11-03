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
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.kotlin.link")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
        mavenLocal()
    }
    
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    
    versionCatalogs {
        create("versions").from("dev.lounres:versions:$versions")
    }
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    }
}