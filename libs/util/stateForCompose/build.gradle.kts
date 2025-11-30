kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.state)
                api(versions.compose.multiplatform.runtime)
            }
        }
    }
}