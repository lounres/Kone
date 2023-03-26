kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
        val jvmExample by getting {
            dependencies {
                implementation(projects.koneNumberTheory)
            }
        }
    }
}