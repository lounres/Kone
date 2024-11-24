kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(versions.logKube.core)
            }
        }
    }
}