kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.contexts)
                api(projects.plugins.suppliedTypes.runtime)
            }
        }
    }
}