plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.contexts)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                api(projects.libs.main.multidimensionalCollections)
                api(projects.libs.main.linearAlgebra)
//                api(projects.libs.main.hooks)
                
                implementation(versions.kotlinx.serialization.core)
            }
        }
    }
}