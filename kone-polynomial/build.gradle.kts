kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.koneCore)
                api(projects.koneAlgebraic)
                implementation(projects.mapUtil)
                implementation(libs.kmath.core)
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