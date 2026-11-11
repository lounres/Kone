pluginDevKit {
    testDataLibraries {
        common(versions.kotlinx.benchmark.runtime)
        common(versions.kotlinx.serialization.core)
        common(versions.kotlinx.serialization.json)
        jvm("org.openjdk.jmh:jmh-core:${versions.versions.jmh.get()}")
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