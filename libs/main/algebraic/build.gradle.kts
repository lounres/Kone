kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.libs.main.core)
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}