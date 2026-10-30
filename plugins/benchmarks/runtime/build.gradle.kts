kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(versions.kotlinx.benchmark.runtime)
            }
        }
        jvmMain {
            dependencies {
                api("org.openjdk.jmh:jmh-core:${versions.versions.jmh.get()}")
            }
        }
    }
}