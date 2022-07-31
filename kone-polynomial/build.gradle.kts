val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()

if (contextReceiversSupportCrunch) {
    kotlin {
        sourceSets {
            main {
                dependencies {
                    api(projects.koneCore)
                    implementation(projects.mapUtil)
                    implementation(libs.kmath.core)
                }
            }
            test {
                dependencies {
                    implementation(projects.koneNumberTheory)
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
                    api(projects.koneCore)
                    implementation(projects.mapUtil)
                    implementation(libs.kmath.core)
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(projects.koneNumberTheory)
                }
            }
        }
    }
}