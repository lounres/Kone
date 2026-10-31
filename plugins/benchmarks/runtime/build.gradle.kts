kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(versions.kotlinx.benchmark.runtime)
                api(versions.kotlinx.serialization.json)
            }
        }
        jvmMain {
            dependencies {
                api("org.openjdk.jmh:jmh-core:${versions.versions.jmh.get()}")
            }
        }
    }
}