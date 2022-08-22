val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()

if (contextReceiversSupportCrunch) {
    kotlin {
        sourceSets {
            main {
                dependencies {
                    implementation(projects.koneCore)
                    implementation(projects.koneAlgebraic)
                    implementation(projects.koneNumberTheory)
                    implementation(projects.konePolynomial)
                    implementation(projects.mapUtil)
                    implementation(libs.kmath.core)
                }
            }
            test {
                dependencies {
                    implementation(projects.testUtil)
                }
            }
        }
    }
} else {
    kotlin {
        @Suppress("UNUSED_VARIABLE")
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(projects.koneCore)
                    implementation(projects.koneAlgebraic)
                    implementation(projects.koneNumberTheory)
                    implementation(projects.konePolynomial)
                    implementation(projects.mapUtil)
                    implementation(libs.kmath.core)
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(projects.testUtil)
                }
            }
        }
    }
}