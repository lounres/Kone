kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.koneCore)
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}