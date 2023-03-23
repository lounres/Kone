kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.computationalContext)
                implementation(projects.libs.main.order)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}