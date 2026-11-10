kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.misc.benchmarks)
            }
        }
        
        jvmMain {
            dependencies {
                api("org.openjdk.jmh:jmh-core:${versions.versions.jmh.get()}")
            }
        }
    }
}