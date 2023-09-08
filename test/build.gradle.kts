kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.polynomial)
            }
        }
    }
}