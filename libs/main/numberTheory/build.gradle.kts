kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.core)
                api(projects.libs.main.algebraic)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.util.kotest)
            }
        }
    }
}