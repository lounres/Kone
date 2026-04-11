plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
                api(projects.libs.main.relations)
                api(projects.libs.main.collections)
                
                implementation(versions.kotlinx.serialization.core)
            }
        }
    }
}