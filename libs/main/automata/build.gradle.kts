kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(versions.kotlinx.coroutines.core)
            }
        }
    }
}