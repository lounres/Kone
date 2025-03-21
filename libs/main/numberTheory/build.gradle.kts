plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
                api(projects.libs.main.annotations)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
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