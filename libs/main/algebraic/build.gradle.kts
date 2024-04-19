kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.comparison)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}