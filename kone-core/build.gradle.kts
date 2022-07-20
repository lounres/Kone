val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()

if (contextReceiversSupportCrunch) {
    kotlin {
        sourceSets {
            main {
                dependencies {

                }
            }
        }
    }
} else {
    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {

                }
            }
            val commonTest by getting {
                dependencies {

                }
            }
        }
    }
}