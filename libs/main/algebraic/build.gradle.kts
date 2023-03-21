kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.computationalContext)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}