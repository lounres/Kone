kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.algebraic)
                implementation(projects.libs.main.comparison)
            }
        }
        commonTest {
            dependencies {
//                implementation(projects.libs.util.kotest)
            }
        }
    }
}