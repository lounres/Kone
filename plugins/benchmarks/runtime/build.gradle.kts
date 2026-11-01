kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.benchmark.runtime)
                api(versions.kotlinx.serialization.core)
                implementation(versions.kotlinx.serialization.json)
            }
        }
        jvmMain {
            dependencies {
                implementation("org.openjdk.jmh:jmh-core:${versions.versions.jmh.get()}")
            }
        }
    }
}