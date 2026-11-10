kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.misc.benchmarks)
            }
        }
    }
}