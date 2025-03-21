plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.util.suppliedTypes)
                api(projects.libs.util.typeSafeRegistry)
                api(projects.libs.main.contexts)
                api(projects.libs.main.relations)
                implementation(projects.libs.main.algebraic)
                implementation(versions.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.main.enumerativeCombinatorics)
            }
        }
    }
}