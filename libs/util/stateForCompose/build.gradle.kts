kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.state)
                api(compose.runtime)
            }
        }
    }
}