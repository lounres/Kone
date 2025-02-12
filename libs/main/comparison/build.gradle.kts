kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.util.suppliedTypes)
                api(projects.libs.util.typeSafeRegistry)
            }
        }
    }
}