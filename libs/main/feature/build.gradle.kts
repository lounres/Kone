kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.collections)
            }
        }
    }
}