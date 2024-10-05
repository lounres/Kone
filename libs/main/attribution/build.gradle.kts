kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}