val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()

if (contextReceiversSupportCrunch) {
    kotlin {
        sourceSets {
            main {
                dependencies {
                    api(projects.koneCore)
                }
            }
        }
    }
} else {
    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    api(projects.koneCore)
                }
            }
            val commonTest by getting {
                dependencies {

                }
            }
        }
    }
}