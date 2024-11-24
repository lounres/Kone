plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(versions.kaml)
            }
        }
    }
}