kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.misc.benchmarks)
                api(versions.kotlinx.serialization.json)
            }
        }
    }
}