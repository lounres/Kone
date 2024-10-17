plugins {
    alias(libs.plugins.kotlinx.atomicfu)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}