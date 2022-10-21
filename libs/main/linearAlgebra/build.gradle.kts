kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.libs.main.core)
                api(projects.libs.main.algebraic)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.libs.util.kotest)
            }
        }
    }
}