plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.maybe)
                
                api(versions.kotlinx.serialization.core)
            }
        }
    }
}