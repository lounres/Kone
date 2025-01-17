kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.core)
                implementation(projects.libs.main.algebraic)
                implementation(projects.libs.main.comparison)
                implementation(projects.libs.main.numberTheory)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}