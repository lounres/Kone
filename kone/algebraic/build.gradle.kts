kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kone.core)
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}