kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.comparison)
            }
        }
        commonTest {
            dependencies {

            }
        }
        jvmExamples {
            dependencies {
                implementation(projects.libs.main.numberTheory)
            }
        }
    }
}