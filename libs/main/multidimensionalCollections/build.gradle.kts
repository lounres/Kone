plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.contexts)
                api(projects.libs.main.collections)
                api(projects.libs.main.relations)
                implementation(projects.libs.main.algebraic)
                
                implementation(versions.kotlinx.serialization.core)
            }
        }
    }
}