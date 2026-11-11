pluginDevKit {
    testDataLibraries {
        common(projects.libs.main.contexts)
        common(projects.libs.main.typeSafeRegistry)
        common(projects.plugins.suppliedTypes.runtime)
        common(projects.libs.main.suppliedTypes)
        common(projects.libs.main.maybe)
        common(versions.kotlinx.serialization.core)
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.mapOperations)
                implementation(projects.libs.util.misc)
            }
        }
    }
}