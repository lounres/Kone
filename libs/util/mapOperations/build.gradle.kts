kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.util.option)
            }
        }
    }
}