kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.numberTheory)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}