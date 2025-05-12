kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
            }
        }
    }
}