kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kone.core)
                api(projects.kone.algebraic)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.utils.kotest)
            }
        }
    }
}