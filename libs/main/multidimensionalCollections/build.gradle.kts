plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
                api(projects.libs.main.relations)
                implementation(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                
                implementation(versions.kotlinx.serialization.core)
            }
        }
    }
}