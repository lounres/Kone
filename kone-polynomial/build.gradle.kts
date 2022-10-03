kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.koneCore)
                api(projects.koneAlgebraic)
                api(libs.kmath.core)
                implementation(projects.mapUtil)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.koneNumberTheory)
                implementation(projects.testUtil)
            }
        }
    }
}