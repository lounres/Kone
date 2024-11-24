plugins {
    alias(versions.plugins.kotlinx.serialization)
    alias(versions.plugins.kotlin.compose)
    alias(versions.plugins.compose.multiplatform)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.serialization.json)
                
                implementation(compose.foundation)
                api(projects.libs.main.computationalGeometry)
            }
        }
    }
}