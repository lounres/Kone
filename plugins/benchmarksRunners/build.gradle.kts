pluginDevKit {
    testDataLibraries {
        common(projects.libs.misc.benchmarks)
        common(versions.kotlinx.serialization.json)
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
            }
        }
    }
}