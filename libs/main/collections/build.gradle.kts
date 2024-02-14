kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.comparison)
            }
        }
    }
}