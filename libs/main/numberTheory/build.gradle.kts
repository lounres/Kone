plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.relations)
                implementation(projects.libs.main.algebraic)
                implementation(projects.libs.main.collections)
                implementation(versions.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.util.kotest)
                implementation(versions.kotlinx.serialization.json)
            }
        }
    }
}