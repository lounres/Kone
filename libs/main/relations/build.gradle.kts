kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.util.suppliedTypes)
                api(projects.libs.util.typeSafeRegistry)
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
            }
        }
    }
}