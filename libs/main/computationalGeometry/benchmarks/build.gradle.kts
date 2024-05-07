plugins {
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(libs.kaml)
            }
        }
    }
}