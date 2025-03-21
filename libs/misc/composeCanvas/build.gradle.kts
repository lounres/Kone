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
                api(projects.libs.main.contexts)
                api(projects.libs.main.core)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                api(projects.libs.main.linearAlgebra)
                api(projects.libs.main.computationalGeometry)
            }
        }
    }
}