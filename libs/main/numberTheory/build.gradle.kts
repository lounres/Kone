kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.comparison)
                implementation(projects.libs.main.algebraic)
                implementation(projects.libs.main.collections)
            }
        }
        commonTest {
            dependencies {
//                implementation(projects.libs.util.kotest)
            }
        }
    }
}