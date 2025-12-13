plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.contexts)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                api(projects.libs.main.multidimensionalCollections)
                api(projects.libs.main.linearAlgebra)
//                api(projects.libs.main.hooks)
                implementation(projects.libs.main.enumerativeCombinatorics)
                
                implementation(versions.kotlinx.serialization.core)
            }
        }
    }
}