kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(pluginDevKit("compiler-plugin-test"))
            }
        }
    }
}