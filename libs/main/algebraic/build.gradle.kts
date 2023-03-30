kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.core)
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