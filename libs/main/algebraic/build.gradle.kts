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
    }
}