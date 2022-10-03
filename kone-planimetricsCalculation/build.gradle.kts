kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.koneCore)
                api(projects.koneAlgebraic)
                api(projects.koneNumberTheory)
                api(projects.konePolynomial)
                api(projects.koneLinearAlgebra)
                implementation(projects.mapUtil)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.testUtil)
            }
        }
    }
}