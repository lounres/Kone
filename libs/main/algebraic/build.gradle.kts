kotlin {
    sourceSets {
        commonMain {
            dependencies {

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