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

include(rootDir.listFiles { file -> file.isDirectory && file.name.startsWith("kone-", ignoreCase = true) }!!.map { it.name })
include(
    "mapUtil",
)