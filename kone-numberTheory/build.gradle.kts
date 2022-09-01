kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.koneCore)
                api(projects.koneAlgebraic)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.testUtil)
            }
        }
    }
}