kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kone.algebraic)
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}