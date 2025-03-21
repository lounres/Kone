plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
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