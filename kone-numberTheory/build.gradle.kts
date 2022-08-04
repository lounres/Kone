val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()

if (contextReceiversSupportCrunch) {
    kotlin {
        sourceSets {
            main {
                dependencies {
                    implementation(projects.koneCore)
                    api(projects.koneAlgebraic)
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
                    api(projects.koneAlgebraic)
                }
            }
            val commonTest by getting {
                dependencies {

                }
            }
        }
    }
}