kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.algebraic)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.util.kotest)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}