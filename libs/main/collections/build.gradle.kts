plugins {
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.comparison)
                implementation(projects.libs.main.algebraic)
                implementation(libs.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.main.enumerativeCombinatorics)
            }
        }
    }
}