kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.order)
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