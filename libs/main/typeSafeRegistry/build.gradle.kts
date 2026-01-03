plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(versions.kotlinx.serialization.core)
            }
        }
    }
}