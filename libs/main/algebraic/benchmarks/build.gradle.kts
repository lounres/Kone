plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.contexts)
                implementation(projects.libs.main.maybe)
                implementation(versions.kotlinx.serialization.json)
            }
        }
    }
}