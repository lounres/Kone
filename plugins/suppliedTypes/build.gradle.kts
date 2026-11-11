pluginDevKit {
    testDataLibraries {
        common(projects.libs.main.suppliedTypes)
        common(versions.kotlinx.serialization.core)
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.mapOperations)
            }
        }
    }
}