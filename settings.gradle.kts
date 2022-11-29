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

include(
    ":libs:main:algebraic",
    ":libs:main:core",
    ":libs:main:linearAlgebra",
    ":libs:main:numberTheory",
    ":libs:main:polynomial",
    ":libs:util:kotest",
    ":libs:util:mapOperations",
)