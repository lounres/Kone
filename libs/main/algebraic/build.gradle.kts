plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.util.suppliedTypes)
                api(projects.libs.util.typeSafeRegistry)
                api(projects.libs.main.comparison)
                implementation(versions.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}