kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.misc.benchmarksJmhSupport)
                api(projects.plugins.benchmarksDeclarative.runtime)
            }
        }
    }
}