kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.relations)
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