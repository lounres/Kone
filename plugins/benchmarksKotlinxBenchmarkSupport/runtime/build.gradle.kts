kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.benchmark.runtime)
            }
        }
        jvmMain {
            dependencies {
                implementation("org.openjdk.jmh:jmh-core:${versions.versions.jmh.get()}")
            }
        }
    }
}