plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
                implementation(projects.libs.main.comparison)
                implementation(projects.libs.main.algebraic)
                
                implementation(versions.kotlinx.serialization.core)
            }
        }
    }
}