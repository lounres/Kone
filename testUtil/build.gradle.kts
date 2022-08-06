val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()

if (contextReceiversSupportCrunch) {
    kotlin {
        sourceSets {
            main {
                dependencies {
                    implementation(libs.kotest.framework.engine)
                    implementation(libs.kotest.framework.datatest)
                    implementation(libs.kotest.assertions.core)
                    implementation(libs.kotest.property)
                }
            }
            test {
                dependencies {

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
                    implementation(libs.kotest.framework.engine)
                    implementation(libs.kotest.framework.datatest)
                    implementation(libs.kotest.assertions.core)
                    implementation(libs.kotest.property)
                }
            }
            val commonTest by getting {
                dependencies {

                }
            }
        }
    }
}