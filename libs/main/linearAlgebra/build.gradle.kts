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
                implementation(projects.libs.main.enumerativeCombinatorics)
                
                implementation(versions.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
//                implementation(projects.libs.util.kotest)
            }
        }
    }
}